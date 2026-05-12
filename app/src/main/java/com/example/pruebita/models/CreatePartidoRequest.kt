package com.example.pruebita.models

data class CreatePartidoRequest(
    val creador_id: Int,
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val nivel_requerido: String,
    val plazas_totales: Int,
    val descripcion: String? = null
)
