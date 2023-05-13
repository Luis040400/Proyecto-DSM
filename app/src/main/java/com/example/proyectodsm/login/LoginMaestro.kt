package com.example.proyectodsm.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.proyectodsm.MainActivity
import com.example.proyectodsm.R
import com.google.firebase.auth.FirebaseAuth

class LoginMaestro : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    private lateinit var progressBar: ProgressDialog
    lateinit var txtUsername: EditText
    lateinit var txtPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_maestro)

        txtUsername = findViewById(R.id.txtUsuario)
        txtPassword = findViewById(R.id.txtPassword)
        //instancias del objeto auth
        auth = FirebaseAuth.getInstance()
        progressBar = ProgressDialog(this)

        val btnIniciarSecion = findViewById<Button>(R.id.btnIniciarSesion)
        btnIniciarSecion.setOnClickListener {
            login()
        }
        val olvidarPassword = findViewById<TextView>(R.id.txtvpassolvidada)
        olvidarPassword.setOnClickListener {
            ReestablecerPassword()
        }

    }
    private fun login() {
        if(txtUsername.text.toString().isEmpty() || txtPassword.text.toString().isEmpty()){
            txtUsername.error="Campo requerido"
            txtPassword.error = "Campo requerido"
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(txtUsername.text.toString().trim()).matches()){
                txtUsername.error="Correo no válido"
            }else{
                progressBar.setMessage("Autenticando")
                progressBar.show()
                auth.signInWithEmailAndPassword(txtUsername.text.toString(), txtPassword.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressBar.hide()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    clear()
                }.addOnFailureListener { exception ->
                    progressBar.hide()
                    Toast.makeText(this, "Error al iniciar $exception", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun ReestablecerPassword()
    {
        auth.sendPasswordResetEmail(txtUsername.text.toString().trim()).addOnCompleteListener{ task ->
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
        txtUsername.setText("")
        txtPassword.setText("")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginPrincipal::class.java))
    }
}