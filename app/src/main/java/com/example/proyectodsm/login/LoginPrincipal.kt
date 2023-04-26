package com.example.proyectodsm.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.proyectodsm.R

class LoginPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_principal)
        val btnAlumno = findViewById<Button>(R.id.btnSesionAlumno)
        val btnMaestro = findViewById<Button>(R.id.btnSesionMaestro)

        btnAlumno.setOnClickListener {
            Toast.makeText(this, "Iniciaste sesion como Alumno", Toast.LENGTH_LONG).show()
        }

        btnMaestro.setOnClickListener {
            val intent: Intent = Intent(this, LoginMaestro::class.java)
            startActivity(intent)
            finish()
        }
    }
}