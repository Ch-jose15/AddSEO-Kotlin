package com.example.addseo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SupportTicketActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etContent: TextInputEditText
    private lateinit var spinnerPriority: AutoCompleteTextView
    private lateinit var spinnerContactMethod: AutoCompleteTextView
    private lateinit var spinnerContactTime: AutoCompleteTextView
    private lateinit var btnSubmit: Button
    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar

    // Constantes para el canal de notificaciones
    companion object {
        private const val CHANNEL_ID = "support_ticket_channel"
        private const val NOTIFICATION_ID = 1
    }

    // Launcher para solicitar permiso de notificaciones
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido, se podrán mostrar notificaciones
            Toast.makeText(
                this,
                "¡Gracias! Ahora podrás recibir notificaciones de tus tickets",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Permiso denegado
            Toast.makeText(
                this,
                "Sin notificaciones no podremos informarte del estado de tus tickets",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_ticket)

        // Inicializar vistas
        etName = findViewById(R.id.etName)
        etContent = findViewById(R.id.etContent)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        spinnerContactMethod = findViewById(R.id.spinnerContactMethod)
        spinnerContactTime = findViewById(R.id.spinnerContactTime)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)

        // Crear canal de notificaciones para Android 8.0+
        createNotificationChannel()

        // Solicitar permiso de notificaciones en Android 13+
        askNotificationPermission()

        // Configurar opciones para los spinners
        setupDropdowns()

        // Botón regresar
        btnBack.setOnClickListener { finish() }

        // Configurar botón de envío
        btnSubmit.setOnClickListener {
            if (validarFormulario()) {
                enviarTicket()
            }
        }
    }

    private fun askNotificationPermission() {
        // Solo es necesario solicitar el permiso en Android 13 (API 33) y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Verificar si el permiso ya está concedido
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si el permiso no está concedido, solicitarlo
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun createNotificationChannel() {
        // Crear el canal de notificación solo en Android 8.0+
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Registrar el canal con el sistema
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification(prioridad: String) {
        // Verificar permiso en tiempo de ejecución
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // No hay permiso, no mostrar notificación
                Toast.makeText(
                    this,
                    "No se pueden mostrar notificaciones. Verifica los permisos.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        // Crear un intent para abrir la aplicación cuando se pulse la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Construir la notificación
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Ticket enviado con éxito")
            .setContentText("Tu ticket de prioridad $prioridad ha sido enviado y será revisado pronto.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Desaparece al tocarla

        // Mostrar la notificación
        with(NotificationManagerCompat.from(this)) {
            try {
                notify(NOTIFICATION_ID, builder.build())
            } catch (e: SecurityException) {
                // Este caso podría ocurrir si los permisos han sido revocados
                Toast.makeText(
                    this@SupportTicketActivity,
                    "No se pudo mostrar notificación: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupDropdowns() {
        // Opciones para prioridad
        val prioridades = arrayOf("Alta", "Media", "Baja")
        val adapterPrioridad = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, prioridades
        )
        spinnerPriority.setAdapter(adapterPrioridad)

        // Opciones para metodo de contacto
        val metodos = arrayOf("Email", "Teléfono", "WhatsApp")
        val adapterMetodo = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, metodos
        )
        spinnerContactMethod.setAdapter(adapterMetodo)

        // Opciones para horario
        val horarios = arrayOf("Mañana (9-2h)", "Tarde (5-7h)")
        val adapterHorario = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, horarios
        )
        spinnerContactTime.setAdapter(adapterHorario)
    }

    private fun validarFormulario(): Boolean {
        var isValid = true

        val nombre = etName.text.toString().trim()
        if (nombre.isEmpty()) {
            etName.error = "Por favor, ingresa tu nombre"
            isValid = false
        }

        val contenido = etContent.text.toString().trim()
        if (contenido.isEmpty()) {
            etContent.error = "Por favor, describe tu consulta"
            isValid = false
        }

        val prioridad = spinnerPriority.text.toString().trim()
        if (prioridad.isEmpty()) {
            spinnerPriority.error = "Selecciona una prioridad"
            isValid = false
        }

        val metodoContacto = spinnerContactMethod.text.toString().trim()
        if (metodoContacto.isEmpty()) {
            spinnerContactMethod.error = "Selecciona un método de contacto"
            isValid = false
        }

        val horarioContacto = spinnerContactTime.text.toString().trim()
        if (horarioContacto.isEmpty()) {
            spinnerContactTime.error = "Selecciona un horario preferido"
            isValid = false
        }

        return isValid
    }

    private fun enviarTicket() {
        // Mostrar barra de progreso
        progressBar.visibility = View.VISIBLE
        btnSubmit.isEnabled = false

        // Obtener datos del formulario
        val nombre = etName.text.toString().trim()
        val contenido = etContent.text.toString().trim()
        val prioridad = spinnerPriority.text.toString().trim()
        val metodoContacto = spinnerContactMethod.text.toString().trim()
        val horarioContacto = spinnerContactTime.text.toString().trim()

        // Usar corrutinas para no bloquear el hilo principal
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val props = Properties()
                props.put("mail.smtp.host", "smtp.gmail.com")
                props.put("mail.smtp.socketFactory.port", "465")
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                props.put("mail.smtp.auth", "true")
                props.put("mail.smtp.port", "465")

                // Configurar la sesión de correo
                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        // IMPORTANTE: Reemplaza con tu correo y contraseña de aplicación
                        return PasswordAuthentication("tecanjos2025@gmail.com", "tdbd xpan qcyv tufc")
                    }
                })

                // Crear el mensaje
                val message = MimeMessage(session)
                message.setFrom(InternetAddress("tecanjos2025@gmail.com"))
                message.addRecipient(Message.RecipientType.TO, InternetAddress("manujarad35@gmail.com"))
                message.subject = "Nuevo ticket de soporte: $prioridad"

                // Cuerpo del mensaje con formato
                val cuerpoMensaje = """
                    Se ha recibido un nuevo ticket de soporte con los siguientes detalles:
                    
                    Nombre: $nombre
                    Prioridad: $prioridad
                    Método de contacto preferido: $metodoContacto
                    Horario preferido: $horarioContacto
                    
                    Descripción del problema:
                    $contenido
                """.trimIndent()

                message.setText(cuerpoMensaje)

                // Enviar el mensaje
                Transport.send(message)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnSubmit.isEnabled = true
                    /*Toast.makeText(
                        this@SupportTicketActivity,
                        "¡Ticket enviado con éxito! Te contactaremos pronto",
                        Toast.LENGTH_LONG
                    ).show()*/

                    // Mostrar notificación al enviar el ticket exitosamente
                    showNotification(prioridad)

                    // Limpiar campos después de enviar
                    limpiarFormulario()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnSubmit.isEnabled = true
                    Toast.makeText(
                        this@SupportTicketActivity,
                        "Error al enviar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun limpiarFormulario() {
        etName.text?.clear()
        etContent.text?.clear()
        spinnerPriority.setText("", false)
        spinnerContactMethod.setText("", false)
        spinnerContactTime.setText("", false)
    }
}