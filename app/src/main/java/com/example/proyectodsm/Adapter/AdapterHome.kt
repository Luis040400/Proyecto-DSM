package com.example.proyectodsm.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodsm.R
import com.example.proyectodsm.model.Exams

class AdapterHome(var con: Context, var list: List<Exams>) :
    RecyclerView.Adapter<AdapterHome.ViewHolder>() {


    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val prueba = v.findViewById<TextView>(R.id.tvPrueba)
        val prueba2 = v.findViewById<TextView>(R.id.tvPrueba2)

        fun bind(exams: Exams) {
            prueba.text = "Titulo: " + exams.prueba
            prueba2.text = "Nombre: " + exams.prueba2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(con).inflate(R.layout.exam_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        /*holder.btnAdd.setOnClickListener{
            onRegistroClickListener.onAgregarClick(item)
        }*/
    }
}