package com.example.addseo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SupportTicketActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etContent: TextInputEditText
    private lateinit var spinnerPriority: AutoCompleteTextView
    private lateinit var spinnerContactMethod: AutoCompleteTextView
    private lateinit var spinnerContactTime: AutoCompleteTextView
    private lateinit var btnSubmit: Button
    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_ticket)

        // Solicitar permiso de notificación en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            val requestCode = 100

            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), requestCode)
            }
        }

        // Inicializar vistas
        etName = findViewById(R.id.etName)
        etContent = findViewById(R.id.etContent)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        spinnerContactMethod = findViewById(R.id.spinnerContactMethod)
        spinnerContactTime = findViewById(R.id.spinnerContactTime)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)

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
        val horarios = arrayOf("Mañana (9-14h)", "Tarde (17-19h)")
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
                val session = javax.mail.Session.getInstance(props, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                        // Cuenta emisora: addseomarketingdigital@gmail.com
                        return javax.mail.PasswordAuthentication("addseomarketingdigital@gmail.com", "kggc idix ycxw icvj")
                    }
                })

                // Crear el mensaje
                val message = javax.mail.internet.MimeMessage(session)
                // El correo se envía desde addseomarketingdigital@gmail.com
                message.setFrom(javax.mail.internet.InternetAddress("addseomarketingdigital@gmail.com", "Soporte AddSEO"))
                // Los tickets llegan a hola@addseo.es
                message.addRecipient(javax.mail.Message.RecipientType.TO, javax.mail.internet.InternetAddress("hola@addseo.es"))
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
                javax.mail.Transport.send(message)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnSubmit.isEnabled = true

                    // Mostrar notificación en lugar de Toast
                    mostrarNotificacion(
                        "Ticket enviado con éxito",
                        "Tu ticket de soporte ha sido recibido. Te contactaremos pronto."
                    )

                    // Limpiar campos después de enviar
                    limpiarFormulario()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnSubmit.isEnabled = true

                    // Mostrar notificación de error
                    mostrarNotificacion(
                        "Error al enviar ticket",
                        "No se pudo enviar: ${e.message}"
                    )
                }
            }
        }
    }

    private fun mostrarNotificacion(titulo: String, mensaje: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "ticket_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 250, 500)) // Patrón de vibración
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Para Android Oreo y superior, se necesita un canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Canal de Tickets",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de tickets de soporte"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Generar un ID único para cada notificación
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun limpiarFormulario() {
        etName.text?.clear()
        etContent.text?.clear()
        spinnerPriority.setText("", false)
        spinnerContactMethod.setText("", false)
        spinnerContactTime.setText("", false)
    }
}