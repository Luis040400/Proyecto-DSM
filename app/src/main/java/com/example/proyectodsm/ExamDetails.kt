package com.example.proyectodsm

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodsm.Adapter.AdapterDetail
import com.example.proyectodsm.model.Exams
import com.example.proyectodsm.model.Pregunta
import com.google.firebase.database.*


private var data: Exams? = null
var dataList = mutableListOf<Pregunta>()

class ExamDetails : Fragment(), AdapterDetail.OnRegistroClickListener {

    private lateinit var database: DatabaseReference
    private lateinit var query: Query
    private lateinit var recyclerView: RecyclerView
    lateinit var adapterDetail: AdapterDetail
    lateinit var eTNombre: EditText
    lateinit var spTiempo: Spinner
    private lateinit var btnMostrarResultados: Button
    lateinit var btnGuardarCambios: Button

    var idExam: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        data = args?.getSerializable("Data") as Exams
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exam_details, container, false)
        recyclerView = view.findViewById(R.id.detail_recyclerView)
        eTNombre = view.findViewById(R.id.eTNombreExam)
        spTiempo = view.findViewById(R.id.spTiempoExam)
        btnMostrarResultados = view.findViewById(R.id.btnMostrarResultados)
        btnMostrarResultados.setOnClickListener {
            moverAStudentList()
        }
        btnGuardarCambios = view.findViewById(R.id.btnGuardarCambiosExamen)
        btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
        database = FirebaseDatabase.getInstance().getReference("examenes")
        query = database.orderByChild("id").equalTo(data!!.id)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
        // Inflate the layout for this fragment

    }

    private fun moverAStudentList() {
        val studentList = StudentsList()
        val args = Bundle()
        args.putString("IDEXAMEN", idExam)
        studentList.arguments = args
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, studentList)
            .commit()
    }

    private fun guardarCambios() {

        if(eTNombre.text.toString().trim().isNotEmpty()){
            val examenMap = mutableMapOf<String,Any>(
                "nombre" to eTNombre.text.toString(),
                "tiempo" to spTiempo.selectedItem.toString().toInt()
            )
            val itemRef = database.orderByChild("id").equalTo(idExam)
            itemRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(examen in snapshot.children){
                        examen.ref.updateChildren(examenMap)
                            .addOnCompleteListener {
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.frameLayout,HomeFragment())
                                    .commit()
                                Toast.makeText(context,"Examen Actualizado",Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,"Error al actualizar",Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            eTNombre.error = "Campo requerido"
        }


    }

    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            for (h in snapshot.children) {
                idExam = h.child("id").getValue(String::class.java).toString()
                eTNombre.setText(h.child("nombre").getValue(String::class.java))
                val spinnerOptions = resources.getStringArray(R.array.tiempo_cantidad)
                val tiempo = h.child("tiempo").getValue(Int::class.java)
                val position = spinnerOptions.indexOf(tiempo.toString())
                spTiempo.setSelection(position)
                for (preguntas in h.child("preguntas").children) {
                    val idPregunta = preguntas.child("idPregunta").getValue(String::class.java)
                    val oA = preguntas.child("oa").getValue(String::class.java)
                    val oB = preguntas.child("ob").getValue(String::class.java)
                    val oC = preguntas.child("oc").getValue(String::class.java)
                    val oD = preguntas.child("od").getValue(String::class.java)
                    val pregunta = preguntas.child("pregunta").getValue(String::class.java)
                    val respuesta = preguntas.child("respuesta").getValue(String::class.java)
                    val tipo = preguntas.child("tipo").getValue(String::class.java)

                    dataList.add(Pregunta(idPregunta, oA, oB, oC, oD, pregunta, respuesta, tipo))
                }
                adapterDetail = AdapterDetail(requireContext(), dataList, this@ExamDetails)
                recyclerView.adapter = adapterDetail
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }

    override fun onStart() {
        super.onStart()
        query.addValueEventListener(valueEventListener)
    }

    override fun onEditarClick(registro: Pregunta) {
        val editFragment = EditFragment()
        val args = Bundle()
        args.putSerializable("Data", registro)
        args.putString("IDEXAMEN", idExam)
        editFragment.arguments = args
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, editFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        dataList.clear()
    }
}