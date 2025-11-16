package com.example.patas_y_colas.data.network

import com.example.patas_y_colas.model.Pet
import retrofit2.Call // <-- ¡Importante!
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Auth (Sin cambios) ---
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // --- Pets (Sin cambios) ---
    @GET("api/pets")
    suspend fun getAllPets(): List<Pet>

    @POST("api/pets")
    suspend fun createPet(@Body pet: Pet): Pet

    @PUT("api/pets/{id}")
    suspend fun updatePet(@Path("id") id: Int, @Body pet: Pet): Pet

    @DELETE("api/pets/{id}")
    suspend fun deletePet(@Path("id") id: Int): Response<Void>

    // --- ¡NUEVO ENDPOINT! ---
    // Esta versión es SÍNCRONA (usa Call) y es la que usará
    // nuestro "recepcionista" (Authenticator) para refrescar el token.
    @POST("api/auth/refresh")
    fun refreshToken(@Body request: Map<String, String>): Call<AuthResponse>
}