package com.example.pruebita.models

data class Partido(
    val id: Int,
    val ubicacion: String,
    val fecha: String,
    val hora: String,
    val nivel_requerido: String
)