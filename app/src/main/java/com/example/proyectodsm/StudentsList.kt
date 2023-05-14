package com.example.proyectodsm

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import com.example.proyectodsm.Adapter.EstudianteAdapter
import com.example.proyectodsm.model.Estudiante
import com.google.firebase.database.*

class StudentsList : Fragment() {

    var idExam: String? = null
    private lateinit var listView: ListView
    private lateinit var btnRegresar: Button
    private lateinit var query: Query
    private lateinit var dataRef: DatabaseReference

    private val estudiantes = ArrayList<Estudiante>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        idExam = args?.getString("IDEXAMEN") as String
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_students_list, container, false)

        listView = view.findViewById(R.id.ListaExamen)
        btnRegresar = view.findViewById(R.id.btnRegresar)
        dataRef = FirebaseDatabase.getInstance().getReference("resultados")
        query = dataRef.orderByChild("idExamen").equalTo(idExam)

        return view
    }

    private val valueEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
           for (h in snapshot.children){
               for(student in h.child("listResults").children){
                   val nombreAlumno = student.child("nombreAlumno").getValue(String::class.java)
                   val nota = student.child("nota").getValue(Long::class.java)
                   estudiantes.add(Estudiante(nombreAlumno.toString(),nota.toString().toLong()))
               }
           }
            listView.adapter = EstudianteAdapter(requireContext(),estudiantes)
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }
    override fun onStart() {
        super.onStart()
        query.addValueEventListener(valueEventListener)
    }

}