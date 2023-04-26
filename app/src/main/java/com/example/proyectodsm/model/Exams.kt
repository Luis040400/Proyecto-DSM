package com.example.proyectodsm.model

data class Exams(
    val id: String,
    val nombre: String?,
    val preguntas: List<Pregunta>?,
    val user_id: String?,
    val tiempo: Int?,
) : java.io.Serializable
