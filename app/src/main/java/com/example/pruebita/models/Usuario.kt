package com.example.pruebita.models

data class Usuario(
    val id: Int,
    val username: String,
    val email: String,
    val nivel: String,
    val partidosJugados: Int,
    val victorias: Int
)