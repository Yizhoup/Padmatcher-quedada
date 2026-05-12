package com.example.pruebita.network

import com.example.pruebita.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("partidos")
    suspend fun getPartidos(): Response<List<Partido>>

    @POST("matches/")
    suspend fun createPartido(
        @Header("Authorization") authorization: String,
        @Body partido: CreatePartidoRequest
    ): Response<Partido>

    @GET("courts")
    suspend fun getCourts(): Response<List<CourtDto>>

    @GET("matches")
    suspend fun getMatches(): Response<List<MatchDto>>

    @GET("players/profile/token/")
    suspend fun getProfile(@Header("Authorization") authorization: String): Response<ProfileResponse>

    @POST("register")
    suspend fun register(@Body user: UserRequest): Response<Unit>

    @POST("login")
    suspend fun login(@Body user: UserRequest): Response<LoginResponse>
}
