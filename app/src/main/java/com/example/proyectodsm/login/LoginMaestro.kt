package com.example.proyectodsm.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.proyectodsm.R
import com.google.firebase.auth.FirebaseAuth

class LoginMaestro : AppCompatActivity() {


    private lateinit var  auth: FirebaseAuth




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_maestro)


        //instancias del objeto auth
        auth = FirebaseAuth.getInstance()




        val btnIniciarSecion = findViewById<Button>(R.id.btnIniciarSesion)


        btnIniciarSecion.setOnClickListener {

            val txtUsername = findViewById<EditText>(R.id.txtUsuario).text.toString()
            val txtPassword = findViewById<EditText>(R.id.txtPassword).text.toString()
            login(txtUsername, txtPassword)

        }

    }


    private fun login(email:String, password:String)
    {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Acceso Exitoso", Toast.LENGTH_LONG).show()
            }

        }.addOnFailureListener { exception ->
            Log.d("error:", exception.toString())
            Log.d("usuario:", email+password)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginPrincipal::class.java))
    }
}