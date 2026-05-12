package com.example.pruebita.models

data class MatchPublic(
    val id: Int,
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val estado: String?,
    val plazas_totales: Int?,
    val creador_id: Int?
)
