package com.example.proyectodsm.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodsm.R
import com.example.proyectodsm.model.Exams
import com.example.proyectodsm.model.Pregunta

class AdapterDetail(
    var con: Context,
    var pregunta: List<Pregunta>,
    private val onRegistroClickListener: OnRegistroClickListener
) :
    RecyclerView.Adapter<AdapterDetail.ViewHolder>() {

    interface OnRegistroClickListener {
        fun onEditarClick(registro: Pregunta)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitulo = v.findViewById<TextView>(R.id.tvTitulo)
        val tvRespuesta = v.findViewById<TextView>(R.id.tvRes)
        val btnEdit = v.findViewById<Button>(R.id.btnEditar)

        fun bind(pregunta: Pregunta) {
            tvTitulo.text = pregunta.pregunta
            tvRespuesta.text = "Respuesta: " + pregunta.respuesta
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDetail.ViewHolder {
        var view = LayoutInflater.from(con).inflate(R.layout.detail_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pregunta.count()
    }
    fun clear() {
        pregunta = emptyList()
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: AdapterDetail.ViewHolder, position: Int) {
        val item = pregunta[position]
        holder.bind(item)

        holder.btnEdit.setOnClickListener{
            onRegistroClickListener.onEditarClick(item)
        }
    }
}