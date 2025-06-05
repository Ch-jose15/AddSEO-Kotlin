package com.example.addseo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ocultar ActionBar
        supportActionBar?.hide()

        // Configurar el texto con "Choose the areas" en negrita
        setupTitleText()

        // Configurar los clicks de las tarjetas
        setupCardClickListeners()

        // 🚨 Crear canal de notificación (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default",
                "General",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // 🚨 Pedir permiso para notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    private fun setupTitleText() {
        val titleTextView = findViewById<TextView>(R.id.titleText)
        val fullText = "Choose the areas\nyou want to work on\nto improve your health"
        val spannableString = SpannableString(fullText)

        // Hacer "Choose the areas" en negrita
        val boldEnd = fullText.indexOf("\n")
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            boldEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        titleTextView.text = spannableString
    }

    private fun setupCardClickListeners() {
        val cardLlamadas = findViewById<CardView>(R.id.cardLlamadas)
        val cardTecnico = findViewById<CardView>(R.id.cardTecnico)
        val cardReuniones = findViewById<CardView>(R.id.cardReuniones)
        val cardAgencias = findViewById<CardView>(R.id.cardAgencias)

        // LLAMADAS - Abrir directamente la app de teléfono (mismo número que en SupportFragment)
        cardLlamadas.setOnClickListener {
            try {
                val phoneNumber = "tel:+34 680318581" // Número de tu empresa
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se puede realizar la llamada", Toast.LENGTH_SHORT).show()
            }
        }

        // TÉCNICO - Abrir SupportTicketActivity directamente
        cardTecnico.setOnClickListener {
            try {
                val intent = Intent(this, SupportTicketActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al abrir tickets", Toast.LENGTH_SHORT).show()
            }
        }

        // REUNIONES - Abrir ScheduleCallActivity directamente
        cardReuniones.setOnClickListener {
            try {
                val intent = Intent(this, ScheduleCallActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al abrir calendario", Toast.LENGTH_SHORT).show()
            }
        }

        // AGENCIAS - Abrir enlace en navegador
        cardAgencias.setOnClickListener {
            try {
                // Cambia esta URL por la URL real del panel de agencias
                val panelUrl = "https://panel.addseo.com" // Reemplaza con tu URL real
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(panelUrl))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se puede abrir el navegador", Toast.LENGTH_SHORT).show()
            }
        }
    }
}