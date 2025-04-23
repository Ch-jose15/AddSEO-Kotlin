package com.example.addseo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleCallActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ScheduleCallActivity"
    }

    // Declaración de vistas
    private lateinit var btnBack: ImageButton
    private lateinit var cardBackButton: CardView
    private lateinit var tvQuestion: TextView
    private lateinit var tvAppointmentId: TextView
    private lateinit var actvTimeZone: AutoCompleteTextView
    private lateinit var timeZoneLayout: TextInputLayout

    // Fechas
    private lateinit var cardDate1: CardView
    private lateinit var cardDate2: CardView
    private lateinit var cardDate3: CardView
    private lateinit var cardDate4: CardView

    // Horas
    private lateinit var cardTime1: MaterialCardView
    private lateinit var cardTime2: MaterialCardView
    private lateinit var cardTime3: MaterialCardView
    private lateinit var cardTime4: MaterialCardView
    private lateinit var cardTime5: MaterialCardView
    private lateinit var cardTime6: MaterialCardView

    // Resumen de cita
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedTime: TextView
    private lateinit var tvSelectedTimeZone: TextView

    // Formulario
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var phoneInputLayout: TextInputLayout
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnSchedule: MaterialButton

    // Variables para controlar selección
    private var selectedDateCard: CardView? = null
    private var selectedTimeCard: MaterialCardView? = null
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var selectedTimeZone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar explícitamente que estamos usando el layout correcto
        Log.d(TAG, "Setting content view to R.layout.activity_schedule_call")
        setContentView(R.layout.activity_schedule_call)

        try {
            // Inicializar vistas con manejo de errores
            initViews()

            // Generar ID de cita
            generateAppointmentId()

            // Configurar valores predeterminados antes de los listeners
            setupDefaultValues()

            // Configurar zonas horarias
            setupTimeZones()

            // Configurar listeners
            setupListeners()

            // Log de depuración
            Log.d(TAG, "Inicialización completada correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error en la inicialización: ${e.message}", e)
            Toast.makeText(this, "Error al inicializar la aplicación: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initViews() {
        try {
            // Elementos de cabecera
            btnBack = findViewById(R.id.btnBack)
            cardBackButton = findViewById(R.id.cardBackButton)
            tvQuestion = findViewById(R.id.tvQuestion)
            tvAppointmentId = findViewById(R.id.tvAppointmentId)

            Log.d(TAG, "Elementos de cabecera inicializados correctamente")

            // Zona horaria
            actvTimeZone = findViewById(R.id.actvTimeZone)
            timeZoneLayout = findViewById(R.id.timeZoneLayout)

            Log.d(TAG, "Elementos de zona horaria inicializados correctamente")

            // Fechas
            cardDate1 = findViewById(R.id.cardDate1)
            cardDate2 = findViewById(R.id.cardDate2)
            cardDate3 = findViewById(R.id.cardDate3)
            cardDate4 = findViewById(R.id.cardDate4)

            Log.d(TAG, "Cards de fechas inicializados correctamente")

            // Horas
            cardTime1 = findViewById(R.id.cardTime1)
            cardTime2 = findViewById(R.id.cardTime2)
            cardTime3 = findViewById(R.id.cardTime3)
            cardTime4 = findViewById(R.id.cardTime4)
            cardTime5 = findViewById(R.id.cardTime5)
            cardTime6 = findViewById(R.id.cardTime6)

            Log.d(TAG, "Cards de horas inicializados correctamente")

            // Resumen de cita
            tvSelectedDate = findViewById(R.id.tvSelectedDate)
            tvSelectedTime = findViewById(R.id.tvSelectedTime)
            tvSelectedTimeZone = findViewById(R.id.tvSelectedTimeZone)

            Log.d(TAG, "TextViews de resumen inicializados correctamente")

            // Formulario
            nameInputLayout = findViewById(R.id.nameInputLayout)
            emailInputLayout = findViewById(R.id.emailInputLayout)
            phoneInputLayout = findViewById(R.id.phoneInputLayout)
            etName = findViewById(R.id.etName)
            etEmail = findViewById(R.id.etEmail)
            etPhone = findViewById(R.id.etPhone)
            btnSchedule = findViewById(R.id.btnSchedule)

            Log.d(TAG, "Elementos de formulario inicializados correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar vistas: ${e.message}", e)
            throw e
        }
    }

    private fun setupDefaultValues() {
        try {
            // Establecer valores predeterminados
            selectedDate = "Martes, 24 de Abril de 2025"
            selectedTime = "10:30"
            selectedTimeZone = "(GMT+01:00) Madrid, España"

            // Actualizar el resumen con los valores predeterminados
            updateSummary()

            // Marcar visualmente las opciones seleccionadas por defecto
            cardDate2.setCardBackgroundColor(getColor(R.color.yellow_light))
            selectedDateCard = cardDate2

            cardTime2.isChecked = true
            selectedTimeCard = cardTime2

            Log.d(TAG, "Valores predeterminados configurados correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al configurar valores predeterminados: ${e.message}", e)
        }
    }

    private fun updateSummary() {
        try {
            runOnUiThread {
                // Actualizar los TextViews del resumen
                tvSelectedDate.text = selectedDate
                tvSelectedTime.text = selectedTime
                tvSelectedTimeZone.text = selectedTimeZone

                // Asegurar que los TextViews están visibles
                tvSelectedDate.visibility = View.VISIBLE
                tvSelectedTime.visibility = View.VISIBLE
                tvSelectedTimeZone.visibility = View.VISIBLE

                Log.d(TAG, "Resumen actualizado: Fecha=$selectedDate, Hora=$selectedTime, Zona=$selectedTimeZone")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar el resumen: ${e.message}", e)
        }
    }

    private fun setupTimeZones() {
        try {
            // Configurar zonas horarias europeas
            val timeZones = arrayOf(
                "(GMT+01:00) Madrid, España",
                "(GMT+01:00) París, Francia",
                "(GMT+01:00) Berlín, Alemania",
                "(GMT+01:00) Roma, Italia",
                "(GMT+00:00) Londres, Reino Unido",
                "(GMT+02:00) Atenas, Grecia",
                "(GMT+02:00) Helsinki, Finlandia"
            )

            // Crear y configurar el adaptador
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, timeZones)
            actvTimeZone.setAdapter(adapter)

            // Establecer el valor inicial explícitamente
            actvTimeZone.setText(timeZones[0], false)
            selectedTimeZone = timeZones[0]

            // Configurar el listener
            actvTimeZone.setOnItemClickListener { _, _, position, _ ->
                selectedTimeZone = timeZones[position]
                Log.d(TAG, "Nueva zona horaria seleccionada: $selectedTimeZone")

                // Actualizar el resumen
                updateSummary()

                // Mostrar feedback al usuario
                Toast.makeText(this, "Zona horaria seleccionada: $selectedTimeZone", Toast.LENGTH_SHORT).show()
            }

            Log.d(TAG, "Zonas horarias configuradas correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al configurar zonas horarias: ${e.message}", e)
        }
    }

    private fun setupListeners() {
        try {
            // Botón de regresar
            btnBack.setOnClickListener {
                Log.d(TAG, "Botón de regresar presionado")
                finish()
            }

            // Configurar listener para las fechas
            setupDateListeners()

            // Configurar listener para las horas
            setupTimeListeners()

            // Botón de reservar
            btnSchedule.setOnClickListener {
                Log.d(TAG, "Botón de reservar presionado")
                if (validateForm()) {
                    confirmAppointment()
                }
            }

            Log.d(TAG, "Todos los listeners configurados correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al configurar listeners: ${e.message}", e)
        }
    }

    private fun setupDateListeners() {
        try {
            // Crear un listener separado para cada fecha para evitar problemas
            cardDate1.setOnClickListener {
                handleDateSelection(cardDate1, "Lunes, 23 de Abril de 2025")
            }

            cardDate2.setOnClickListener {
                handleDateSelection(cardDate2, "Martes, 24 de Abril de 2025")
            }

            cardDate3.setOnClickListener {
                handleDateSelection(cardDate3, "Miércoles, 25 de Abril de 2025")
            }

            cardDate4.setOnClickListener {
                handleDateSelection(cardDate4, "Jueves, 26 de Abril de 2025")
            }

            Log.d(TAG, "Listeners de fecha configurados correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al configurar listeners de fecha: ${e.message}", e)
        }
    }

    private fun handleDateSelection(cardView: CardView, dateText: String) {
        try {
            // Resetear todos los fondos
            cardDate1.setCardBackgroundColor(getColor(R.color.white))
            cardDate2.setCardBackgroundColor(getColor(R.color.white))
            cardDate3.setCardBackgroundColor(getColor(R.color.white))
            cardDate4.setCardBackgroundColor(getColor(R.color.white))

            // Establecer el fondo seleccionado
            cardView.setCardBackgroundColor(getColor(R.color.yellow_light))
            selectedDateCard = cardView

            // Actualizar la fecha seleccionada
            selectedDate = dateText
            Log.d(TAG, "Nueva fecha seleccionada: $selectedDate")

            // Actualizar el resumen
            updateSummary()

            // Mostrar feedback al usuario
            Toast.makeText(this, "Fecha seleccionada: $selectedDate", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error al manejar selección de fecha: ${e.message}", e)
        }
    }

    private fun setupTimeListeners() {
        try {
            // Crear un listener separado para cada hora para evitar problemas
            cardTime1.setOnClickListener {
                handleTimeSelection(cardTime1, "09:00")
            }

            cardTime2.setOnClickListener {
                handleTimeSelection(cardTime2, "10:30")
            }

            cardTime3.setOnClickListener {
                handleTimeSelection(cardTime3, "12:00")
            }

            cardTime4.setOnClickListener {
                handleTimeSelection(cardTime4, "15:00")
            }

            cardTime5.setOnClickListener {
                handleTimeSelection(cardTime5, "16:30")
            }

            cardTime6.setOnClickListener {
                handleTimeSelection(cardTime6, "18:00")
            }

            Log.d(TAG, "Listeners de hora configurados correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al configurar listeners de hora: ${e.message}", e)
        }
    }

    private fun handleTimeSelection(cardView: MaterialCardView, timeText: String) {
        try {
            // Desmarcar todas las tarjetas
            cardTime1.isChecked = false
            cardTime2.isChecked = false
            cardTime3.isChecked = false
            cardTime4.isChecked = false
            cardTime5.isChecked = false
            cardTime6.isChecked = false

            // Establecer la tarjeta seleccionada
            cardView.isChecked = true
            selectedTimeCard = cardView

            // Actualizar la hora seleccionada
            selectedTime = timeText
            Log.d(TAG, "Nueva hora seleccionada: $selectedTime")

            // Actualizar el resumen
            updateSummary()

            // Mostrar feedback al usuario
            Toast.makeText(this, "Hora seleccionada: $selectedTime", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error al manejar selección de hora: ${e.message}", e)
        }
    }

    private fun generateAppointmentId() {
        try {
            // Generar un ID de cita basado en la fecha actual
            val dateFormat = SimpleDateFormat("yyMMddHHmm", Locale.getDefault())
            val appointmentId = "CT-" + dateFormat.format(Date()) + "-" + (1000..9999).random()
            tvAppointmentId.text = "ID de cita: $appointmentId"
            Log.d(TAG, "ID de cita generado: $appointmentId")
        } catch (e: Exception) {
            Log.e(TAG, "Error al generar ID de cita: ${e.message}", e)
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        try {
            // Validar el nombre
            if (etName.text.toString().trim().isEmpty()) {
                nameInputLayout.error = "Por favor, ingresa tu nombre"
                isValid = false
                Log.d(TAG, "Validación: Nombre vacío")
            } else {
                nameInputLayout.error = null
            }

            // Validar el email
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                emailInputLayout.error = "Por favor, ingresa tu correo electrónico"
                isValid = false
                Log.d(TAG, "Validación: Email vacío")
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.error = "Por favor, ingresa un correo electrónico válido"
                isValid = false
                Log.d(TAG, "Validación: Email inválido")
            } else {
                emailInputLayout.error = null
            }

            // Validar el teléfono
            if (etPhone.text.toString().trim().isEmpty()) {
                phoneInputLayout.error = "Por favor, ingresa tu número de teléfono"
                isValid = false
                Log.d(TAG, "Validación: Teléfono vacío")
            } else {
                phoneInputLayout.error = null
            }

            // Validar que se haya seleccionado fecha y hora
            if (selectedDateCard == null) {
                Toast.makeText(this, "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show()
                isValid = false
                Log.d(TAG, "Validación: Fecha no seleccionada")
            }

            if (selectedTimeCard == null) {
                Toast.makeText(this, "Por favor, selecciona una hora", Toast.LENGTH_SHORT).show()
                isValid = false
                Log.d(TAG, "Validación: Hora no seleccionada")
            }

            Log.d(TAG, "Validación del formulario: $isValid")
        } catch (e: Exception) {
            Log.e(TAG, "Error en la validación del formulario: ${e.message}", e)
            isValid = false
        }

        return isValid
    }

    private fun confirmAppointment() {
        try {
            // Obtener los datos del formulario
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            Log.d(TAG, "Confirmando cita para: $name, Email: $email, Teléfono: $phone")
            Log.d(TAG, "Detalles de la cita: Fecha=$selectedDate, Hora=$selectedTime, Zona=$selectedTimeZone")

            // Mensaje de confirmación con detalles completos
            val confirmationMessage = "¡Cita reservada con éxito!\n" +
                    "Fecha: $selectedDate\n" +
                    "Hora: $selectedTime\n" +
                    "Zona horaria: $selectedTimeZone\n" +
                    "Recibirás un correo de confirmación en $email."

            // Mostrar mensaje de confirmación
            Toast.makeText(this, confirmationMessage, Toast.LENGTH_LONG).show()

            // Limpiar el formulario
            etName.setText("")
            etEmail.setText("")
            etPhone.setText("")

            Log.d(TAG, "Formulario limpiado y cita confirmada")
        } catch (e: Exception) {
            Log.e(TAG, "Error al confirmar la cita: ${e.message}", e)
            Toast.makeText(this, "Error al confirmar la cita: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}