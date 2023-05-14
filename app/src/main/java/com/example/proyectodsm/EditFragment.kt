package com.example.proyectodsm

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.proyectodsm.model.Pregunta
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*

private var data: Pregunta? = null
var idExam: String? = null

class EditFragment : Fragment() {

    lateinit var idPregunta: String
    lateinit var spTipo: Spinner
    lateinit var eTPregunta: EditText
    lateinit var eTOpcionA: EditText
    lateinit var eTOpcionB: EditText
    lateinit var eTOpcionC: EditText
    lateinit var eTOpcionD: EditText
    lateinit var eTRespuesta: EditText
    lateinit var navView: NavigationView
    lateinit var btnGuardar: Button
    private lateinit var mProgressBar: ProgressDialog
    lateinit var contenedorRespuestas: LinearLayout

    private lateinit var dataRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        data = args?.getSerializable("Data") as Pregunta
        idExam = args?.getString("IDEXAMEN") as String
        mProgressBar = ProgressDialog(requireContext())
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit, container, false)
        dataRef = FirebaseDatabase.getInstance().getReference("examenes")
        spTipo = view.findViewById(R.id.spTEditipoExamen)
        eTPregunta = view.findViewById(R.id.eTEditPregunta)
        eTOpcionA = view.findViewById(R.id.eToptionA)
        eTOpcionB = view.findViewById(R.id.eToptionB)
        eTOpcionC = view.findViewById(R.id.eToptionC)
        eTOpcionD = view.findViewById(R.id.eToptionD)
        eTRespuesta = view.findViewById(R.id.eTEditRespuestaCorrecta)
        btnGuardar = view.findViewById(R.id.btnGuardarCambios)
        contenedorRespuestas = view.findViewById(R.id.respuestasMultiple)

        navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        init()
        return view
    }

    @SuppressLint("ResourceType")
    private fun init() {
        btnGuardar.setOnClickListener {
            guardarCambios()
        }
        idPregunta = data!!.idPregunta.toString()
        val indiceElemento =
            resources.getStringArray(R.array.tipo_examen).indexOf(data!!.tipo.toString())
        spTipo.setSelection(indiceElemento)
        eTPregunta.setText(data!!.pregunta)

        eTRespuesta.setText(data!!.respuesta)
        if (data!!.tipo.toString() == "Selección Multiple") {
            eTOpcionA.setText(data!!.oA)
            eTOpcionB.setText(data!!.oB)
            eTOpcionC.setText(data!!.oC)
            eTOpcionD.setText(data!!.oD)
        } else {
            contenedorRespuestas.visibility = View.GONE
        }
    }

    private fun guardarCambios() {
        var oA: String? = null
        var oB: String? = null
        var oC: String? = null
        var oD: String? = null
        if (spTipo.selectedItem == "Selección Multiple") {
            oA = eTOpcionA.text.toString()
            oB = eTOpcionB.text.toString()
            oC = eTOpcionC.text.toString()
            oD = eTOpcionD.text.toString()
        } else if (spTipo.selectedItem == "Verdadero o Falso") {
            oA = eTOpcionA.text.toString()
            oB = eTOpcionB.text.toString()
            oC = ""
            oD = ""
        }
        val preguntaMap = mutableMapOf<String, Any>(
            "idPregunta" to idPregunta,
            "pregunta" to eTPregunta.text.toString(),
            "respuesta" to eTRespuesta.text.toString(),
            "tipo" to spTipo.selectedItem.toString()
        )
        oA?.let { preguntaMap["oA"] = it }
        oB?.let { preguntaMap["oB"] = it }
        oC?.let { preguntaMap["oC"] = it }
        oD?.let { preguntaMap["oD"] = it }
        val itemRef = dataRef.orderByChild("id").equalTo(idExam)
        itemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (examenSnapshot in snapshot.children) {
                    examenSnapshot.ref.child("preguntas").orderByChild("idPregunta")
                        .equalTo(idPregunta).addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (preguntaSnapshot in snapshot.children) {
                                    preguntaSnapshot.ref.updateChildren(preguntaMap)
                                        .addOnCompleteListener {
                                            Toast.makeText(
                                                context,
                                                "Actualizado Correctamente",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            requireActivity().supportFragmentManager.popBackStack()
                                        }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    context,
                                    "Error al editar el registro",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al editar el registro", Toast.LENGTH_SHORT).show()
            }
        })
    }
}