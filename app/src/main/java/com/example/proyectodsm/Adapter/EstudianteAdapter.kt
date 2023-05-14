package com.example.proyectodsm.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.proyectodsm.R
import com.example.proyectodsm.model.Estudiante

class EstudianteAdapter(context: Context, estudiantes: ArrayList<Estudiante>)
    : ArrayAdapter<Estudiante>(context, 0, estudiantes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false)
        }

        val estudiante = getItem(position)
        val nombreTextView = view?.findViewById<TextView>(R.id.nombreTextView)
        nombreTextView?.text = "Nombre: "+estudiante?.nombre

        val notaTextView = view?.findViewById<TextView>(R.id.notaTextView)
        notaTextView?.text = "Nota: "+estudiante?.nota.toString()

        return view!!
    }
}