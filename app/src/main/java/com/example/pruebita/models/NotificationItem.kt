package com.example.pruebita.models

data class NotificationItem(
    val id: Int,
    val usuario_id: Int?,
    val partido_id: Int?,
    val tipo: String,
    val leida: Boolean,
    val creado_en: String?
)

