package com.example.pruebita.models

data class Inscription(
    val id: Int,
    val partido_id: Int,
    val usuario_id: Int,
    val estado: String,
    val inscrito_en: String?
)
