package com.example.proyectodsm

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.example.proyectodsm.Adapter.AdapterHome
import com.example.proyectodsm.model.Exams
import com.example.proyectodsm.model.Pregunta
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class CreateExamFragment(val preguntas: Int, val tiempo: Int) : Fragment() {

    lateinit var eTPregunta: EditText
    lateinit var etPosibleRespuesta: EditText
    lateinit var eTRespuestaCorrecta: EditText
    lateinit var eTNombreExamen: EditText
    private lateinit var mProgressBar: ProgressDialog

    lateinit var navView: NavigationView

    lateinit var spTipoPregunta: Spinner

    lateinit var btnAgregar: Button
    lateinit var btnGuardar: Button
    lateinit var btnCrearExamen: Button


    lateinit var contenedor: LinearLayout
    lateinit var listView: ListView

    lateinit var tvTitulo: TextView

    private val elements = ArrayList<String>()
    private lateinit var adapterArray: ArrayAdapter<String>
    private lateinit var adapterHome: AdapterHome
    lateinit var selectedItem: String
    val database = FirebaseDatabase.getInstance()
    val databaseRef = database.reference.child("examenes")
    private val preguntasList = mutableListOf<Pregunta>()
    private lateinit var auth: FirebaseAuth
    private var number = 1
    lateinit var user: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProgressBar = ProgressDialog(requireContext())
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_exam, container, false)
        eTNombreExamen = view.findViewById(R.id.eTNombreExamen)
        eTPregunta = view.findViewById(R.id.eTPregunta)
        etPosibleRespuesta = view.findViewById(R.id.eTPosibleRespuesta)
        eTRespuestaCorrecta = view.findViewById(R.id.eTRespuestaCorrecta)
        spTipoPregunta = view.findViewById(R.id.spTipoExamen)
        contenedor = view.findViewById(R.id.opcionMultipleContenedor)
        listView = view.findViewById(R.id.listView)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        btnAgregar = view.findViewById(R.id.btnAgregar)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnCrearExamen = view.findViewById(R.id.btnCrearExamen)

        tvTitulo = view.findViewById(R.id.tvTitulo)
        tvTitulo.text = "Pregunta $number"

        eTPregunta.isEnabled = false
        eTRespuestaCorrecta.isEnabled = false

        spTipoPregunta.onItemSelectedListener = spinnerListener
        btnGuardar.setOnClickListener {
            guardarPregunta()
        }
        btnCrearExamen.setOnClickListener {
            crearExamen()
        }
        return view
    }

    private fun crearExamen() {
        mProgressBar.setMessage("Creando examen")
        mProgressBar.show()
        val examen = Exams(
            UUID.randomUUID().toString(),
            eTNombreExamen.text.toString(),
            preguntasList,
            user.uid,
            tiempo,
        )
        val examenRef = databaseRef.push()
        examenRef.setValue(examen)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Examen guardado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    mProgressBar.hide()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, HomeFragment())
                        .commit()
                    navView.setCheckedItem(R.id.nav_home)
                    parentFragmentManager.popBackStack()
                } else {
                    mProgressBar.hide()
                    Toast.makeText(
                        requireContext(),
                        "Error al agregar el registro ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun guardarPregunta() {
        if (number >= preguntas) {
            tvTitulo.text = "Guardar Examen"
            btnGuardar.visibility = View.GONE
            btnCrearExamen.visibility = View.VISIBLE
            spTipoPregunta.isEnabled = false
            eTPregunta.isEnabled = false
            eTRespuestaCorrecta.isEnabled = false
            Toast.makeText(
                requireContext(),
                "Ya se han agregado todas las preguntas",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (preguntasList.size < preguntas) {
            if (selectedItem == "Selección Multiple") {
                if (eTPregunta.text.toString().trim().isNotEmpty() || eTRespuestaCorrecta.text.toString().trim()
                        .isNotEmpty() || eTNombreExamen.text.toString().trim().isNotEmpty() || elements.size < 0
                ) {

                    eTNombreExamen.isEnabled = false
                    var oA: String? = elements[0]
                    var oB: String? = elements[1]
                    var oC: String? = elements[2]
                    var oD: String? = elements[3]
                    val pregunta = Pregunta(
                        UUID.randomUUID().toString(),
                        oA, oB, oC, oD, eTPregunta.text.toString(),
                        eTRespuestaCorrecta.text.toString(),
                        selectedItem
                    )
                    preguntasList.add(pregunta)
                    number += 1
                    if(number > preguntas) {
                        tvTitulo.text = "Crear examen"
                    }else{
                        tvTitulo.text = "Pregunta $number"
                    }
                    eTPregunta.text.clear()
                    eTRespuestaCorrecta.text.clear()
                    spTipoPregunta.setSelection(0)
                    elements.clear()
                    eTPregunta.isEnabled = false
                    eTRespuestaCorrecta.isEnabled = false
                    contenedor.visibility = View.GONE
                } else {
                    if (eTRespuestaCorrecta.text.toString().trim()
                            .isEmpty()
                    ) eTRespuestaCorrecta.error = "Campo requerido"
                    if (eTPregunta.text.toString().trim().isEmpty()) eTPregunta.error =
                        "Campo requerido"
                    if(eTNombreExamen.text.toString().trim().isEmpty()) eTNombreExamen.error="Campo requerido"
                    if(elements.size<0) Toast.makeText(requireContext(),"No se han agregado respuestas",Toast.LENGTH_SHORT).show()
                }

            } else if (selectedItem == "Verdadero o Falso") {
                if (eTPregunta.text.toString().trim()
                        .isNotEmpty() || eTRespuestaCorrecta.text.toString().trim()
                        .isNotEmpty() || eTNombreExamen.text.toString().trim().isNotEmpty() || eTNombreExamen.text.toString().trim().isNotEmpty()
                ) {

                    eTNombreExamen.isEnabled = false
                    var oA: String? = "Verdadero"
                    var oB: String? = "Falso"
                    var oC: String? = ""
                    var oD: String? = ""

                    val pregunta = Pregunta(
                        UUID.randomUUID().toString(),
                        oA,
                        oB,
                        oC,
                        oD,
                        eTPregunta.text.toString(),
                        eTRespuestaCorrecta.text.toString(),
                        selectedItem
                    )
                    preguntasList.add(pregunta)
                    number += 1
                    if(number > preguntas) {
                        tvTitulo.text = "Crear examen"
                    }else{
                        tvTitulo.text = "Pregunta $number"
                    }
                    eTPregunta.text.clear()
                    eTRespuestaCorrecta.text.clear()
                    spTipoPregunta.setSelection(0)
                    eTPregunta.isEnabled = false
                    eTRespuestaCorrecta.isEnabled = false
                } else {
                    if (eTRespuestaCorrecta.text.toString().trim()
                            .isNotEmpty()
                    ) eTRespuestaCorrecta.error = "Campo requerido"
                    if (eTPregunta.text.toString().trim().isNotEmpty()) eTPregunta.error =
                        "Campo requerido"
                    if(eTNombreExamen.text.toString().trim().isEmpty()) eTNombreExamen.error="Campo requerido"
                    if(elements.size<0) Toast.makeText(requireContext(),"No se han agregado respuestas",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            selectedItem = parent.getItemAtPosition(position).toString()
            if (selectedItem == "Selección Multiple") {
                contenedor.visibility = View.VISIBLE
                eTPregunta.isEnabled = true
                eTRespuestaCorrecta.isEnabled = true
                multipleSelection()
            } else if (selectedItem == "Verdadero o Falso") {
                eTPregunta.isEnabled = true
                eTRespuestaCorrecta.isEnabled = true
                contenedor.visibility = View.GONE
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Código a ejecutar cuando no se selecciona nada en el Spinner
        }
    }

    private fun multipleSelection() {

        adapterArray = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, elements)
        listView.adapter = adapterArray

        btnAgregar.setOnClickListener {
            val newElement = etPosibleRespuesta.text.toString()
            if (newElement.isNotEmpty()) {
                if (elements.size < 4) {
                    elements.add(newElement)
                    adapterArray.notifyDataSetChanged()
                    etPosibleRespuesta.text.clear()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Maxima cantidad de posibles respuestas",
                        Toast.LENGTH_SHORT
                    ).show()
                    etPosibleRespuesta.text.clear()
                }
            } else {
                etPosibleRespuesta.error = "Campo requerido"
            }
        }
        listView.setOnItemLongClickListener { parent, view, position, id ->
            elements.removeAt(position)
            adapterArray.notifyDataSetChanged()
            true
        }
    }
}