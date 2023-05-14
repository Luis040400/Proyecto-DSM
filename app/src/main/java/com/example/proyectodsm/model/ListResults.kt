package com.example.proyectodsm.model

data class ListResults(
    val nombreAlumno: String?,
    val nota: Float?,
    val respuestas: List<Answers>
)
