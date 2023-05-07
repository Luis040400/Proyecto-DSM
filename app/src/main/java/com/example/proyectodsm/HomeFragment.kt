package com.example.proyectodsm

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodsm.Adapter.AdapterHome
import com.example.proyectodsm.model.Exams
import com.example.proyectodsm.model.Pregunta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class HomeFragment : Fragment(), AdapterHome.OnDeleteClickListener {


    private lateinit var database: DatabaseReference
    private lateinit var dataRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterHome
    private lateinit var examItem: LinearLayout

    private lateinit var mProgressBar: ProgressDialog

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProgressBar = ProgressDialog(requireContext())
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        database = FirebaseDatabase.getInstance().reference
        dataRef = FirebaseDatabase.getInstance().getReference("examenes")
        auth = FirebaseAuth.getInstance()
        recyclerView = view.findViewById(R.id.home_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }


    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val dataList = mutableListOf<Exams>()
            for (h in snapshot.children) {
                val uuid = h.child("id").getValue(String::class.java)
                val nombre = h.child("nombre").getValue(String::class.java)
                val preguntas = ArrayList<Pregunta>()
                val user = h.child("user_id").getValue(String::class.java)
                val tiempo = h.child("tiempo").getValue(Int::class.java)

                for(preguntasSnapshot in h.child("preguntas").children){
                    val idPregunta = preguntasSnapshot.child("idPregunta").getValue(String::class.java)
                    val oA = preguntasSnapshot.child("oa").getValue(String::class.java)
                    val oB = preguntasSnapshot.child("ob").getValue(String::class.java)
                    val oC = preguntasSnapshot.child("oc").getValue(String::class.java)
                    val oD = preguntasSnapshot.child("od").getValue(String::class.java)
                    val pregunta = preguntasSnapshot.child("pregunta").getValue(String::class.java)
                    val respuesta = preguntasSnapshot.child("respuesta").getValue(String::class.java)
                    val tipo = preguntasSnapshot.child("tipo").getValue(String::class.java)

                    preguntas.add(Pregunta(idPregunta,oA,oB,oC,oD,pregunta,respuesta,tipo))
                }


                if (nombre != null && preguntas != null && user != null) {
                    val data = Exams(
                        uuid.toString(),
                        nombre,
                        preguntas,
                        user,
                        tiempo,
                    )
                    dataList.add(data)
                }
                adapter = AdapterHome(requireContext(), dataList, this@HomeFragment)
                recyclerView.adapter = adapter
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(ContentValues.TAG, "Error al leer los datos", error.toException())
        }
    }

    override fun onStart() {
        super.onStart()
        val usuarioQuery = dataRef.orderByChild("user_id").equalTo(auth.currentUser!!.uid)
        usuarioQuery.addValueEventListener(valueEventListener)
    }

    override fun onStop() {
        super.onStop()
        dataRef.removeEventListener(valueEventListener)
    }

    override fun onDeleteClick(exams: Exams) {
        AlertDialog.Builder(context)
            .setTitle("Eliminar examen")
            .setMessage("¿Desea eliminar este examen?")
            .setPositiveButton("Eliminar"){dialog, _ ->
                mProgressBar.setMessage("Eliminando Registro")
                mProgressBar.show()
                val itemRef = dataRef.orderByChild("id").equalTo(exams.id)
                itemRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (examenSnapshot in snapshot.children) {
                            examenSnapshot.ref.removeValue().addOnSuccessListener {
                                mProgressBar.hide()
                                Toast.makeText(context, "Registro Eliminado", Toast.LENGTH_SHORT).show()

                            }.addOnFailureListener {
                                mProgressBar.hide()
                                Toast.makeText(context, "Error al eliminar el registro", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Error al eliminar el registro", Toast.LENGTH_SHORT).show()
                    }
                })
                adapter.notifyDataSetChanged()
            }.setNegativeButton("Cancelar",null)
            .show()
    }

    override fun onDetailClick(exams: Exams) {
        val fragment = ExamDetails()
        val args = Bundle()
        args.putSerializable("Data", exams)
        fragment.arguments = args
        val fragmentManager = requireFragmentManager()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()


    }

}