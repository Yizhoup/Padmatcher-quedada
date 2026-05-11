package com.example.pruebita.repository

import com.example.pruebita.network.RetrofitClient

class CourtRepository {

    suspend fun getCourts() = RetrofitClient.api.getCourts()
}