package com.example.pruebita.network

import com.example.pruebita.models.CreatePartidoRequest
import com.example.pruebita.models.CancelInscriptionResponse
import com.example.pruebita.models.Inscription
import com.example.pruebita.models.JoinMatchResponse
import com.example.pruebita.models.LoginResponse
import com.example.pruebita.models.MatchPublic
import com.example.pruebita.models.PlayerPublic
import com.example.pruebita.models.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("matches/")
    suspend fun createPartido(
        @Header("Authorization") authorization: String,
        @Body partido: CreatePartidoRequest
    ): Response<MatchPublic>

    @GET("matches/available/")
    suspend fun getAvailableMatches(
        @Header("Authorization") authorization: String
    ): Response<List<MatchPublic>>

    @POST("matches/{matchId}/inscripciones/")
    suspend fun joinMatch(
        @Path("matchId") matchId: Int,
        @Header("Authorization") authorization: String
    ): Response<JoinMatchResponse>

    @PATCH("matches/{matchId}/inscripciones/cancel")
    suspend fun cancelInscription(
        @Path("matchId") matchId: Int,
        @Header("Authorization") authorization: String
    ): Response<CancelInscriptionResponse>

    @GET("inscriptions/")
    suspend fun getMyInscriptions(
        @Header("Authorization") authorization: String
    ): Response<List<Inscription>>

    @GET("players/")
    suspend fun getPlayers(
        @Header("Authorization") authorization: String
    ): Response<List<PlayerPublic>>

    @GET("players/profile/token/")
    suspend fun getProfile(
        @Header("Authorization") authorization: String
    ): Response<ProfileResponse>

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password"
    ): Response<LoginResponse>
}
