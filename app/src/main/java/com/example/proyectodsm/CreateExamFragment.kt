package com.example.proyectodsm

import android.annotation.SuppressLint
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class CreateExamFragment(val preguntas: Int, val tiempo: Int) : Fragment() {

    lateinit var spTipoPregunta: Spinner
    lateinit var eTPregunta: EditText
    lateinit var contenedor: LinearLayout
    lateinit var listView: ListView
    lateinit var btnAgregar: Button
    lateinit var btnGuardar: Button
    lateinit var btnCrearExamen: Button
    lateinit var etPosibleRespuesta: EditText
    lateinit var eTRespuestaCorrecta: EditText
    lateinit var tvTitulo: TextView
    lateinit var eTNombreExamen: EditText
    private val elements = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var adapterHome: AdapterHome
    lateinit var selectedItem: String
    val database = FirebaseDatabase.getInstance()
    val databaseRef = database.reference.child("examenes")
    private val preguntasList = mutableListOf<Pregunta>()
    private lateinit var auth: FirebaseAuth
    private var number = preguntasList.size + 1
    lateinit var user: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_exam, container, false)
        spTipoPregunta = view.findViewById(R.id.spTipoExamen)
        eTPregunta = view.findViewById(R.id.eTPregunta)
        contenedor = view.findViewById(R.id.opcionMultipleContenedor)
        listView = view.findViewById(R.id.listView)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        btnAgregar = view.findViewById(R.id.btnAgregar)
        etPosibleRespuesta = view.findViewById(R.id.eTPosibleRespuesta)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnCrearExamen = view.findViewById(R.id.btnCrearExamen)
        eTRespuestaCorrecta = view.findViewById(R.id.eTRespuestaCorrecta)
        tvTitulo = view.findViewById(R.id.tvTitulo)
        eTNombreExamen = view.findViewById(R.id.eTNombreExamen)
        tvTitulo.text = "Pregunta $number"
        spTipoPregunta.onItemSelectedListener = spinnerListener
        btnGuardar.setOnClickListener {
            guardarPregunta()
        }
        btnCrearExamen.setOnClickListener {
            crearExamen()
        }
        // Obtener la ventana actual del fragmento
        val window = requireActivity().window
        // Establecer el modo de entrada suave de la ventana en "adjustPan"
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        // Inflate the layout for this fragment
        return view
    }

    private fun crearExamen() {
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
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, HomeFragment())
                        .commit()
                    parentFragmentManager.popBackStack()
                }else{
                    Toast.makeText(requireContext(), "Error al agregar el registro ${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun guardarPregunta() {
        number += 1
        tvTitulo.text = "Pregunta $number"
        if (number > preguntas) {
            btnGuardar.visibility = View.GONE
            btnCrearExamen.visibility = View.VISIBLE
            tvTitulo.text = "Crear Examen"
            spTipoPregunta.isEnabled = false
            eTPregunta.isEnabled = false
            eTRespuestaCorrecta.isEnabled = false
        }
        if (preguntasList.size < preguntas) {
            eTNombreExamen.isEnabled = false
            if (selectedItem == "Selección Multiple") {
                var oA: String? = elements[0]
                var oB: String? = elements[1]
                var oC: String? = elements[2]
                var oD: String? = elements[3]
                val pregunta = Pregunta(
                    oA,
                    oB,
                    oC,
                    oD,
                    eTPregunta.text.toString(),
                    eTRespuestaCorrecta.text.toString(),
                    selectedItem
                )
                preguntasList.add(pregunta)
                eTPregunta.text.clear()
                eTRespuestaCorrecta.text.clear()
                elements.clear()
                spTipoPregunta.setSelection(0)
                adapter.clear()
                adapterHome.notifyDataSetChanged()
                contenedor.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Pregunta ${preguntasList.size} guardada",
                    Toast.LENGTH_SHORT
                ).show()

            }
            else if(selectedItem == "Verdadero o Falso"){
                var oA: String? = "Verdadero"
                var oB: String? = "Falso"
                var oC: String? = ""
                var oD: String? = ""
                val pregunta = Pregunta(
                    oA,
                    oB,
                    oC,
                    oD,
                    eTPregunta.text.toString(),
                    eTRespuestaCorrecta.text.toString(),
                    selectedItem
                )
                preguntasList.add(pregunta)
                eTPregunta.text.clear()
                eTRespuestaCorrecta.text.clear()
                elements.clear()
                spTipoPregunta.setSelection(0)
                adapter.clear()
                adapterHome.notifyDataSetChanged()
                contenedor.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Pregunta ${preguntasList.size} guardada",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            Toast.makeText(
                requireContext(),
                "Ya se han agregado todas las preguntas",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            selectedItem = parent.getItemAtPosition(position).toString()
            if (selectedItem == "Selección Multiple") {
                contenedor.visibility = View.VISIBLE
                multipleSelection()
            } else if (selectedItem == "Verdadero o Falso") {
                contenedor.visibility = View.GONE
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Código a ejecutar cuando no se selecciona nada en el Spinner
        }
    }

    private fun multipleSelection() {

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, elements)
        listView.adapter = adapter

        btnAgregar.setOnClickListener {
            val newElement = etPosibleRespuesta.text.toString()
            if (newElement.isNotEmpty()) {
                if(elements.size > 4){
                    elements.add(newElement)
                    adapter.notifyDataSetChanged()
                    etPosibleRespuesta.text.clear()
                }
                else{
                    Toast.makeText(requireContext(),"Maxima cantidad de posibles respuestas",Toast.LENGTH_SHORT).show()
                    etPosibleRespuesta.text.clear()
                }
            }
        }
        listView.setOnItemLongClickListener { parent, view, position, id ->
            elements.removeAt(position)
            adapter.notifyDataSetChanged()
            true
        }
    }
}