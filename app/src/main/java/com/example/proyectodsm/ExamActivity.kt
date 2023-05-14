package com.example.proyectodsm

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.*
import com.example.proyectodsm.model.Answers
import com.example.proyectodsm.model.ListResults
import com.example.proyectodsm.model.Pregunta
import com.google.firebase.database.*
import java.math.BigDecimal
import java.math.RoundingMode

class ExamActivity : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val databaseRef = database.reference.child("examenes")
    val databaseResult = database.reference.child("resultados")

    private lateinit var query: Query
    var studentName = ""
    var codeExam = ""
    var preguntasLis = ArrayList<Pregunta>()
    var tiempo = 0
    var nombre = ""
    var porcentajePregunta = 0.0f
    var nota = 0.0f
    var currentQuestion = ""
    var respuestaSeleccionada = ""

    private lateinit var txtvCronometro:TextView
    private lateinit var mProgressBar: ProgressDialog
    private lateinit var tvExamName: TextView
    private lateinit var tvPregunta: TextView

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioOptionA: RadioButton
    private lateinit var radioOptionB: RadioButton
    private lateinit var radioOptionC: RadioButton
    private lateinit var radioOptionD: RadioButton

    private lateinit var containerPreguntra: LinearLayout
    private lateinit var containerRespuesta: LinearLayout
    private var countDownTimer: CountDownTimer? = null
    private val respuestaList = mutableListOf<Answers>()

    private lateinit var btnNext: Button
    private lateinit var btnFinalizar: Button

    var currentQuestionIndex = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)
        mProgressBar = ProgressDialog(this)
        mProgressBar.setMessage("Obteniendo datos...")
        mProgressBar.setCancelable(false)
        studentName = intent.getStringExtra("StudentName").toString()
        codeExam = intent.getStringExtra("CodeExam").toString()

        query = databaseRef.orderByChild("id").equalTo(codeExam)

        tvExamName = findViewById(R.id.tvExamName)
        tvPregunta = findViewById(R.id.tvPregunta)
        radioGroup = findViewById(R.id.radioGroup)
        radioOptionA = findViewById(R.id.optionA)
        radioOptionB = findViewById(R.id.optionB)
        radioOptionC = findViewById(R.id.optionC)
        radioOptionD = findViewById(R.id.optionD)
        txtvCronometro = findViewById(R.id.txtvCronometro)

        containerPreguntra = findViewById(R.id.containerPregunta)
        containerRespuesta = findViewById(R.id.containerRespuesta)
        btnNext = findViewById(R.id.btnSiguiente)
        btnFinalizar = findViewById(R.id.btnFinalizar)

        btnNext.setOnClickListener {
            var id: Int = radioGroup.checkedRadioButtonId
            if (id != -1) {
                val radio: RadioButton = findViewById(id)
                respuestaSeleccionada = radio.text as String
                if (respuestaSeleccionada.trim() == preguntasLis[currentQuestionIndex].respuesta.toString()
                        .trim()
                ) {
                    nota += porcentajePregunta
                    val respuesta = Answers(
                        preguntasLis[currentQuestionIndex].pregunta.toString(),
                        respuestaSeleccionada,
                        preguntasLis[currentQuestionIndex].respuesta.toString(),
                        true
                    )
                    respuestaList.add(respuesta)
                } else {
                    val respuesta = Answers(
                        preguntasLis[currentQuestionIndex].pregunta.toString(),
                        respuestaSeleccionada,
                        preguntasLis[currentQuestionIndex].respuesta.toString(),
                        false
                    )
                    respuestaList.add(respuesta)
                }
            }
            currentQuestionIndex++
            if (currentQuestionIndex <= preguntasLis.size - 1) {
                tvPregunta.text = preguntasLis[currentQuestionIndex].pregunta
                mostrarPregunta()
            } else {
                btnNext.visibility = View.GONE
                btnFinalizar.visibility = View.VISIBLE
            }
        }
        btnFinalizar.setOnClickListener {
            finishExam()
        }
    }

    private fun finishExam() {
        val notaRedondeado =
            BigDecimal(nota.toDouble()).setScale(2, RoundingMode.HALF_DOWN).toFloat()
        val listResults = ListResults(
            studentName, notaRedondeado, respuestaList
        )
        val itemRef = databaseResult.orderByChild("idExamen").equalTo(codeExam)
        itemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (resultado in snapshot.children) {
                    resultado.ref.child("listResults").push().setValue(listResults)
                        .addOnCompleteListener {
                            detenerContador()
                            Toast.makeText(
                                this@ExamActivity,
                                "Resultados Guardados",
                                Toast.LENGTH_SHORT
                            ).show()
                            val dialog = Dialog(this@ExamActivity)
                            dialog.setContentView(R.layout.dialog_score)
                            dialog.findViewById<TextView>(R.id.tvNota).text = notaRedondeado.toString()
                            dialog.show()
                            dialog.findViewById<Button>(R.id.btnContinuar).setOnClickListener {
                                dialog.dismiss()
                                val intent = Intent(this@ExamActivity,StudentLogin::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                this@ExamActivity,
                                "Error al guardar los resultados",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ExamActivity,
                    "Error guardar las respuestas",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (h in snapshot.children) {
                nombre = h.child("nombre").getValue(String::class.java).toString()
                tiempo = h.child("tiempo").getValue(Int::class.java).toString().toInt()
                for (preguntas in h.child("preguntas").children) {
                    val idPregunta = preguntas.child("idPregunta").getValue(String::class.java)
                    val oA = preguntas.child("oa").getValue(String::class.java)
                    val oB = preguntas.child("ob").getValue(String::class.java)
                    val oC = preguntas.child("oc").getValue(String::class.java)
                    val oD = preguntas.child("od").getValue(String::class.java)
                    val pregunta = preguntas.child("pregunta").getValue(String::class.java)
                    val respuesta = preguntas.child("respuesta").getValue(String::class.java)
                    val tipo = preguntas.child("tipo").getValue(String::class.java)
                    preguntasLis.add(
                        Pregunta(
                            idPregunta,
                            oA,
                            oB,
                            oC,
                            oD,
                            pregunta,
                            respuesta,
                            tipo
                        )
                    )
                }
            }
            porcentajePregunta = 10 / preguntasLis.size.toFloat()
            tvExamName.text = nombre
            mostrarPregunta()
            setearTiempo()
            mProgressBar.dismiss()

        }

        override fun onCancelled(error: DatabaseError) {
            mProgressBar.dismiss()
        }
    }

    private fun setearTiempo() {
        val tiempomio = tiempo.toLong()
        txtvCronometro
        var segundos =  tiempomio*1000
        var minutos= tiempomio*60*1000
        var horas = tiempomio*60*60*1000
        var tiempoMiliSegundos = segundos+minutos

        countDownTimer = object: CountDownTimer(tiempoMiliSegundos,1000){
            override fun onFinish() {
                this.cancel()
                finishExam()
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

    private fun detenerContador(){
        countDownTimer?.cancel()
    }
    fun mostrarPregunta() {
        radioGroup.clearCheck()
        if (currentQuestionIndex > preguntasLis.size - 1) {
            radioOptionA.visibility = View.GONE
            radioOptionB.visibility = View.GONE
            radioOptionC.visibility = View.GONE
            radioOptionD.visibility = View.GONE
        } else {
            tvPregunta.text = preguntasLis[currentQuestionIndex].pregunta
            if (preguntasLis[currentQuestionIndex].tipo == "Verdadero o Falso") {
                radioOptionA.text = preguntasLis[currentQuestionIndex].oA
                radioOptionB.text = preguntasLis[currentQuestionIndex].oB
                radioOptionC.visibility = View.GONE
                radioOptionD.visibility = View.GONE
                radioOptionC.text = preguntasLis[currentQuestionIndex].oC
                radioOptionD.text = preguntasLis[currentQuestionIndex].oD
                containerPreguntra.visibility = View.VISIBLE
                containerRespuesta.visibility = View.VISIBLE
            } else if (preguntasLis[currentQuestionIndex].tipo == "Selección Multiple") {
                radioOptionA.visibility = View.VISIBLE
                radioOptionB.visibility = View.VISIBLE
                radioOptionC.visibility = View.VISIBLE
                radioOptionD.visibility = View.VISIBLE
                radioOptionA.text = preguntasLis[currentQuestionIndex].oA
                radioOptionB.text = preguntasLis[currentQuestionIndex].oB
                radioOptionC.text = preguntasLis[currentQuestionIndex].oC
                radioOptionD.text = preguntasLis[currentQuestionIndex].oD
                containerPreguntra.visibility = View.VISIBLE
                containerRespuesta.visibility = View.VISIBLE
            }

        }
    }

    override fun onStart() {
        super.onStart()
        mProgressBar?.show()
        query.addValueEventListener(valueEventListener)
    }

}