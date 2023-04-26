package com.example.proyectodsm.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodsm.R
import com.example.proyectodsm.model.Exams
import com.google.firebase.database.DatabaseReference

class AdapterHome(var con: Context, val list: List<Exams>,private val onDeleteClickListener: OnDeleteClickListener) :
    RecyclerView.Adapter<AdapterHome.ViewHolder>() {

    interface OnDeleteClickListener{
        fun onDeleteClick(exams: Exams)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val prueba = v.findViewById<TextView>(R.id.tvTituloExam)
        var btnEliminar = v.findViewById<Button>(R.id.btnEliminar)

        fun bind(exams: Exams,listener: OnDeleteClickListener) {
            prueba.text = exams.nombre
            btnEliminar.setOnClickListener{listener.onDeleteClick(exams)}
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
        holder.bind(item,onDeleteClickListener)
    }
}