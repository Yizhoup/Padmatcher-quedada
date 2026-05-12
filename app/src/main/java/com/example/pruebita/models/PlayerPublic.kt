package com.example.pruebita.models

data class PlayerPublic(
    val id: Int,
    val nombre: String,
    val email: String,
    val nivel: String?,
    val ciudad: String?,
    val rol: String?
)
