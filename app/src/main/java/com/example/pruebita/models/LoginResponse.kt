package com.example.pruebita.models

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val username: String
)