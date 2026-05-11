package com.example.pruebita.models

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val token: String
)