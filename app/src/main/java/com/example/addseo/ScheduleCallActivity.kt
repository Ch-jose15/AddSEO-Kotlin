package com.example.addseo

import android.app.AlertDialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull


class ScheduleCallActivity : AppCompatActivity() {

    private lateinit var dateContainer: LinearLayout
    private lateinit var timeContainer: LinearLayout
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedTime: TextView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnSchedule: MaterialButton

    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_call)

        // Referencias
        dateContainer = findViewById(R.id.dateContainer)
        timeContainer = findViewById(R.id.timeContainer)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        btnSchedule = findViewById(R.id.btnSchedule)

        generateDateButtons()
        generateTimeButtons()

        btnSchedule.setOnClickListener {
            sendAppointment()
        }
    }

    private fun generateDateButtons() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        val today = Calendar.getInstance()

        for (i in 1..5) {
            today.add(Calendar.DAY_OF_YEAR, 1)
            val dateStr = dateFormat.format(today.time)
            val displayStr = displayFormat.format(today.time)

            val button = Button(this).apply {
                text = displayStr
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
                setOnClickListener {
                    selectedDate = dateStr
                    tvSelectedDate.text = "Fecha seleccionada: $dateStr"
                }
            }
            dateContainer.addView(button)
        }
    }

    private fun generateTimeButtons() {
        val hours = listOf(9, 10, 11, 12, 13, 14, 16, 17, 18, 19)
        for (hour in hours) {
            val timeStr = String.format("%02d:00", hour)
            val button = Button(this).apply {
                text = timeStr
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
                setOnClickListener {
                    selectedTime = timeStr
                    tvSelectedTime.text = "Hora seleccionada: $timeStr"
                }
            }
            timeContainer.addView(button)
        }
    }

    private fun sendAppointment() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Combinar fecha y hora en formato ISO 8601 para Google Calendar (Make Free compatible)
        val startDate = "${selectedDate}T${selectedTime}:00"

        val json = JSONObject().apply {
            put("name", name)
            put("email", email)
            put("phone", phone)
            put("startDate", startDate)
        }

        val client = OkHttpClient()
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url("https://hook.eu2.make.com/d96vv1mfr4rfa12hgb4vccidfwf50jm8")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ScheduleCallActivity, "Error al enviar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        // Limpiar campos
                        etName.text.clear()
                        etEmail.text.clear()
                        etPhone.text.clear()
                        selectedDate = null
                        selectedTime = null
                        tvSelectedDate.text = "Fecha seleccionada:"
                        tvSelectedTime.text = "Hora seleccionada:"

                        // Mostrar popup
                        AlertDialog.Builder(this@ScheduleCallActivity)
                            .setTitle("¡Éxito!")
                            .setMessage("Cita agendada correctamente")
                            .setPositiveButton("Aceptar", null)
                            .show()
                    } else {
                        Toast.makeText(
                            this@ScheduleCallActivity,
                            "Error: ${response.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })
    }
}
