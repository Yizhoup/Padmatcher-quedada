package com.example.pruebita.models

data class ProfileResponse(
    val valid: Boolean,
    val player: ProfilePlayer
)

data class ProfilePlayer(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String,
    val nivel: String?,
    val ciudad: String?
)
