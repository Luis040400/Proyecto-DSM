package com.example.proyectodsm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodsm.Adapter.AdapterHome
import com.example.proyectodsm.model.Exams


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterHome
    private val dataList = mutableListOf<Exams>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false)
        initialize()
        recyclerView = view.findViewById(R.id.home_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdapterHome(requireContext(),dataList)
        recyclerView.adapter = adapter
        return view
    }

    private fun initialize() {
    }

}