package com.example.patas_y_colas.data.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit // <--- IMPORTANTE: Agregado para manejar el tiempo

object TokenManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_TOKEN = "jwt_token"

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TOKEN, null)
    }
}

object RetrofitClient {
    // Esta es la URL que tenías en tu archivo, asegúrate que sea la de tu Render activo
    private const val BASE_URL = "https://backend-movil-1hs0.onrender.com/"

    fun getClient(context: Context): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            // --- AJUSTE CRÍTICO PARA RENDER ---
            .connectTimeout(60, TimeUnit.SECONDS) // Espera hasta 60 segundos al conectar
            .readTimeout(60, TimeUnit.SECONDS)    // Espera hasta 60 segundos al leer respuesta
            .writeTimeout(60, TimeUnit.SECONDS)   // Espera hasta 60 segundos al enviar datos
            // ----------------------------------
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                val token = TokenManager.getToken(context)
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}