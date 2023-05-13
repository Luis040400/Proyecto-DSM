package com.example.proyectodsm.alumno

import android.content.IntentSender.OnFinished
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.proyectodsm.R




class Cronometro_Examen : AppCompatActivity() {

    private lateinit var txtTiempo:EditText
    private lateinit var txtvCronometro:TextView
    private lateinit var btnPlay:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cronometro_examen)

        txtTiempo = findViewById(R.id.txtTiempo)
        txtvCronometro = findViewById(R.id.txtvCronometro)
        btnPlay = findViewById(R.id.btnPlay)



        btnPlay.setOnClickListener {
            play()
        }
    }

    private fun play(){
        var segundos =  txtTiempo.text.toString().toLong()*1000
        var minutos= txtTiempo.text.toString().toLong()*60*1000
        var horas = txtTiempo.text.toString().toLong()*60*60*1000
        var tiempoMiliSegundos = segundos+minutos

        object: CountDownTimer(tiempoMiliSegundos,1000){
            override fun onFinish() {

                this.cancel()
            }

            override fun onTick(milisUntilFinished:Long) {
                var tiempoSegundos =(milisUntilFinished/1000).toInt()
                val horas=tiempoSegundos/3600
                tiempoSegundos=tiempoSegundos%3600
                val minutos = tiempoSegundos/60
                tiempoSegundos = tiempoSegundos%60
                txtvCronometro.text=horas.toString().padStart(2,'0')+":"+ minutos.toString().padStart(2,'0')+": "+ tiempoSegundos.toString().padStart(2,'0')
                Log.d("", txtvCronometro.text.toString())
            }
        }.start()
    }


}