package com.example.pruebita.models

data class Partido(
    val id: Int,
    val club: String,
    val fecha: String,
    val hora: String,
    val jugadoresActuales: Int,
    val jugadoresMaximos: Int,
    val nivel: String
)