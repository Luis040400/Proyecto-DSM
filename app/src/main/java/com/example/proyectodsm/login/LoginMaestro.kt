package com.example.proyectodsm.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.proyectodsm.MainActivity
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

        val olvidarPassword = findViewById<TextView>(R.id.txtvpassolvidada)
        olvidarPassword.setOnClickListener {
            val txtUsername = findViewById<EditText>(R.id.txtUsuario).text.toString()

            ReestablecerPassword(txtUsername)

        }


    }


    private fun login(email:String, password:String)
    {
        if(email=="" || password=="")
        {
            Toast.makeText(this, "Campos Vacios", Toast.LENGTH_LONG).show()
        }
        else
        {
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                Toast.makeText(this, "E-mail invalido", Toast.LENGTH_LONG).show()
            }
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                clear()

            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error al iniciar, Revisa tus credenciales", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun ReestablecerPassword(email:String)
    {
        auth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
            if(task.isSuccessful)
            {
                Toast.makeText(this, "Revisa tu correo", Toast.LENGTH_LONG).show()
            }
            else
            {
                Toast.makeText(this, "NO PASO NADA", Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun clear(){
        val txtUsername = findViewById<EditText>(R.id.txtUsuario)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        txtUsername.setText("")
        txtPassword.setText("")
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginPrincipal::class.java))
    }
}