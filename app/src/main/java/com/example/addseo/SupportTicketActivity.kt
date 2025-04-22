package com.example.addseo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.addseo.tickets_soporte

class tickets_soporte {

    private lateinit var binding: tickets_soporte

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Configurar el botón de regresar
        binding.btnBack.setOnClickListener {
            finish() // Cierra esta actividad y regresa a la anterior
        }

        // Configurar el botón de enviar
        binding.btnSubmit.setOnClickListener {
            if (validateForm()) {
                sendTicket()
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validar el nombre
        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.nameInputLayout.error = "Por favor, ingresa tu nombre"
            isValid = false
        } else {
            binding.nameInputLayout.error = null
        }

        // Validar el contenido de la consulta
        if (binding.etContent.text.toString().trim().isEmpty()) {
            binding.contentInputLayout.error = "Por favor, detalla tu consulta"
            isValid = false
        } else {
            binding.contentInputLayout.error = null
        }

        return isValid
    }

    private fun sendTicket() {
        // Obtener los datos del formulario
        val name = binding.etName.text.toString().trim()
        val content = binding.etContent.text.toString().trim()

        // Aquí implementarías la lógica para enviar el ticket a tu sistema
        // Por ejemplo, una llamada a una API o guardar en una base de datos local

        // Ejemplo de simulación de envío exitoso
        Toast.makeText(
            this,
            "Ticket enviado correctamente. Gracias, $name!",
            Toast.LENGTH_LONG
        ).show()

        // Limpiar el formulario después del envío exitoso
        binding.etName.text?.clear()
        binding.etContent.text?.clear()

        // Opcional: regresar a la pantalla anterior después de un envío exitoso
        // finish()
    }
}