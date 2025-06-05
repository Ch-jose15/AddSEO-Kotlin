package com.example.addseo.fragments

import ReviewFragment
import SupportFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.addseo.R

class FragmentContainerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        // Obtener qué fragmento mostrar
        val fragmentType = intent.getStringExtra("fragment_type") ?: "support"
        val fragmentTitle = intent.getStringExtra("fragment_title") ?: "AddSEO"

        // Configurar el título
        supportActionBar?.title = fragmentTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Cargar el fragmento correspondiente
        if (savedInstanceState == null) {
            val fragment: Fragment = when (fragmentType) {
                "support" -> SupportFragment()
                "review" -> ReviewFragment()
                else -> SupportFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}