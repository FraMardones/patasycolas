package com.example.patas_y_colas.data.network

import com.example.patas_y_colas.model.Pet
import okhttp3.MultipartBody // <-- ¡NUEVO IMPORT!
import okhttp3.RequestBody // <-- ¡NUEVO IMPORT!
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

data class CatFactResponse(
    val fact: String,
    val length: Int
)

interface ApiService {

    // --- Auth (Sin cambios) ---
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // --- Pets (MODIFICADO) ---

    // Este se mantiene igual, obtiene la lista de mascotas del usuario
    @GET("api/pets")
    suspend fun getAllPets(): List<Pet>

    // --- ¡MODIFICADO! ---
    // Ya no usa @Body, ahora es @Multipart
    @Multipart
    @POST("api/pets")
    suspend fun createPet(
        @Part("name") name: RequestBody,
        @Part("species") species: RequestBody,
        @Part("breed") breed: RequestBody,
        @Part("age") age: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part imageFile: MultipartBody.Part // El archivo de imagen
    ): Pet

    // --- ¡MODIFICADO! ---
    // También usa @Multipart por si se quiere cambiar la foto
    @Multipart
    @PUT("api/pets/{id}")
    suspend fun updatePet(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("species") species: RequestBody,
        @Part("breed") breed: RequestBody,
        @Part("age") age: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part imageFile: MultipartBody.Part? // La imagen es opcional al actualizar
    ): Pet

    // Este se mantiene igual
    @DELETE("api/pets/{id}")
    suspend fun deletePet(@Path("id") id: Int): Response<Void>

    // --- Otros (Sin cambios) ---
    @POST("api/auth/refresh")
    fun refreshToken(@Body request: Map<String, String>): Call<AuthResponse>

    @GET("https://catfact.ninja/fact")
    suspend fun getCatFact(): CatFactResponse
}