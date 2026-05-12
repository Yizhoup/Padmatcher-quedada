package com.example.pruebita.network

import com.example.pruebita.models.AchievementsResponse
import com.example.pruebita.models.CourtDto
import com.example.pruebita.models.LoginRequest
import com.example.pruebita.models.MatchDto
import com.example.pruebita.models.Partido
import com.example.pruebita.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<User>

    @GET("users")
    suspend fun getUsers(): retrofit2.Response<List<User>>

    @GET("courts")
    suspend fun getCourts(): List<CourtDto>

    @GET("partidos")
    suspend fun getPartidos(): List<Partido>

    @GET("matches")
    suspend fun getMatches(): List<MatchDto>

    @GET("users/{id}/achievements")
    suspend fun getAchievements(
        @Path("id") userId: Int
    ): AchievementsResponse
}