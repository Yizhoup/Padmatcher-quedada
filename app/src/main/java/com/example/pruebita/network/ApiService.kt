package com.example.pruebita.network

import com.example.pruebita.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("partidos")
    suspend fun getPartidos(): Response<List<Partido>>

    @GET("courts")
    suspend fun getCourts(): Response<List<CourtDto>>

    @GET("matches")
    suspend fun getMatches(): Response<List<MatchDto>>

    @POST("register")
    suspend fun register(@Body user: UserRequest): Response<Unit>

    @POST("login")
    suspend fun login(@Body user: UserRequest): Response<LoginResponse>
}