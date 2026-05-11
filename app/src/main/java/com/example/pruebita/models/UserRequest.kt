package com.example.pruebita.models

data class UserRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val ciudad: String
)