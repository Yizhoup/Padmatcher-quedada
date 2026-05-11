package com.example.pruebita.repository

import com.example.pruebita.network.RetrofitClient

class MatchRepository {

    suspend fun getMatches() = RetrofitClient.api.getMatches()
}