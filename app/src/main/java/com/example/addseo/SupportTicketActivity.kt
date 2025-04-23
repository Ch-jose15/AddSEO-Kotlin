package com.example.addseo

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SupportTicketActivity : AppCompatActivity() {

    // Declaración de vistas
    private lateinit var btnBack: ImageButton
    private lateinit var cardBackButton: CardView
    private lateinit var tvQuestion: TextView
    private lateinit var tvTicketNumber: TextView
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var contentInputLayout: TextInputLayout
    private lateinit var etName: EditText
    private lateinit var etContent: EditText
    private lateinit var btnSubmit: MaterialButton
    private lateinit var tvPriorityInfo: TextView
    private lateinit var tvEstimatedTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_ticket)

        // Inicializar vistas
        initViews()
        setupListeners()
        generateTicketNumber()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        cardBackButton = findViewById(R.id.cardBackButton)
        tvQuestion = findViewById(R.id.tvQuestion)
        tvTicketNumber = findViewById(R.id.tvTicketNumber)
        nameInputLayout = findViewById(R.id.nameInputLayout)
        contentInputLayout = findViewById(R.id.contentInputLayout)
        etName = findViewById(R.id.etName)
        etContent = findViewById(R.id.etContent)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvPriorityInfo = findViewById(R.id.tvPriorityInfo)
        tvEstimatedTime = findViewById(R.id.tvEstimatedTime)
    }

    private fun setupListeners() {
        // Configurar el botón de regresar
        btnBack.setOnClickListener {
            finish() // Cierra esta actividad y regresa a la anterior
        }

        // Configurar el botón de enviar
        btnSubmit.setOnClickListener {
            if (validateForm()) {
                sendTicket()
            }
        }

        // Mostrar/ocultar información de prioridad al hacer clic
        tvPriorityInfo.setOnClickListener {
            tvEstimatedTime.visibility = if (tvEstimatedTime.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    private fun generateTicketNumber() {
        // Generar un número de ticket basado en la fecha actual
        val dateFormat = SimpleDateFormat("yyMMddHHmm", Locale.getDefault())
        val ticketId = "TK-" + dateFormat.format(Date()) + "-" + (1000..9999).random()
        tvTicketNumber.text = "ID de ticket: $ticketId"
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validar el nombre
        if (etName.text.toString().trim().isEmpty()) {
            nameInputLayout.error = "Por favor, ingresa tu nombre"
            isValid = false
        } else {
            nameInputLayout.error = null
        }

        // Validar el contenido de la consulta
        if (etContent.text.toString().trim().isEmpty()) {
            contentInputLayout.error = "Por favor, detalla tu consulta"
            isValid = false
        } else if (etContent.text.toString().trim().length < 20) {
            contentInputLayout.error = "Tu descripción es demasiado corta. Por favor, sé más específico"
            isValid = false
        } else {
            contentInputLayout.error = null
        }

        return isValid
    }

    private fun sendTicket() {
        // Obtener los datos del formulario
        val name = etName.text.toString().trim()
        val content = etContent.text.toString().trim()

        // Aquí implementar la lógica para enviar el ticket a tu sistema

        // Ejemplo de simulación de envío exitoso
        Toast.makeText(
            this,
            "¡Ticket enviado con éxito! Te responderemos pronto, $name.",
            Toast.LENGTH_LONG
        ).show()

        // Limpiar el formulario después del envío exitoso
        etName.setText("")
        etContent.setText("")

        // Generar un nuevo número de ticket
        generateTicketNumber()
    }
}