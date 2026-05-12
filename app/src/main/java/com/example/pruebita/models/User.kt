package com.example.pruebita.models

data class User(
    val id: Int,
    val nombre: String,
    val nivel: String?,
    val ciudad: String?,
    val online: Boolean
)