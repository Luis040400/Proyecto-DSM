package com.example.proyectodsm

import android.annotation.SuppressLint
import android.content.Intent
import android.app.Dialog
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class StudentLogin : AppCompatActivity() {

    lateinit var tvName: TextView
    lateinit var tvCode: TextView
    lateinit var btnStart: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_student_login)
        tvName = findViewById(R.id.studentName)
        tvCode = findViewById(R.id.codeExam)
        btnStart = findViewById(R.id.btnStart)

        btnStart.setOnClickListener {
            val dialog = Dialog(this@StudentLogin)
            dialog.setContentView(R.layout.dialog_student)
            dialog.findViewById<Button>(R.id.btnAceptar).setOnClickListener {
                dialog.dismiss()
                val intent = Intent(this,ExamActivity::class.java)
                intent.putExtra("StudentName", tvName.text.toString())
                intent.putExtra("CodeExam", tvCode.text.toString())
                startActivity(intent)
                finish()
            }
            dialog.findViewById<Button>(R.id.btnCancelar).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

    }
}