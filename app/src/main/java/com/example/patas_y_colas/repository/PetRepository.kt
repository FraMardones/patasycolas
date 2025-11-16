package com.example.patas_y_colas.repository

import android.content.Context
import com.example.patas_y_colas.data.network.LoginRequest
import com.example.patas_y_colas.data.network.RegisterRequest
import com.example.patas_y_colas.data.network.RetrofitClient
import com.example.patas_y_colas.data.network.TokenManager
import com.example.patas_y_colas.model.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PetRepository(private val context: Context) {

    private val api = RetrofitClient.getClient(context)

    // (Tu StateFlow para las mascotas está perfecto)
    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val allPets: StateFlow<List<Pet>> = _pets.asStateFlow()

    // Función para recargar la lista desde la API
    suspend fun refreshPets() {
        try {
            val petsFromApi = api.getAllPets()
            _pets.value = petsFromApi
        } catch (e: Exception) {
            e.printStackTrace()
            _pets.value = emptyList() // En caso de error, lista vacía o manejar caché
        }
    }

    // --- Login ---
    suspend fun login(email: String, pass: String): Boolean {
        return try {
            val response = api.login(LoginRequest(email, pass))
            if (response.isSuccessful && response.body() != null) {
                // --- CORRECCIÓN AQUÍ ---
                val authResponse = response.body()!!
                // Usamos la nueva función para guardar AMBOS tokens
                TokenManager.saveTokens(context, authResponse.token, authResponse.refreshToken)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Registro ---
    suspend fun register(nombre: String, apellido: String, email: String, pass: String): Boolean {
        return try {
            val request = RegisterRequest(
                firstname = nombre,
                lastname = apellido,
                email = email,
                password = pass
            )
            val response = api.register(request)
            if (response.isSuccessful && response.body() != null) {
                // --- CORRECCIÓN AQUÍ ---
                val authResponse = response.body()!!
                // Usamos la nueva función para guardar AMBOS tokens
                TokenManager.saveTokens(context, authResponse.token, authResponse.refreshToken)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- ¡NUEVA FUNCIÓN! ---
    // Esta es la función que llamarás desde tu botón de "Cerrar Sesión"
    fun logout() {
        // Borra los tokens del dispositivo
        TokenManager.clearTokens(context)
        // Limpia la lista de mascotas en memoria para que no se vean en la UI
        _pets.value = emptyList()
    }


    // --- Crear mascota ---
    // (Esta función no necesita cambios, el "Authenticator" hará la magia)
    suspend fun insert(pet: Pet) {
        try {
            api.createPet(pet)
            refreshPets()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- Actualizar mascota ---
    // (Esta función no necesita cambios)
    suspend fun update(pet: Pet) {
        val idToUpdate = pet.id
        if (idToUpdate != null) {
            try {
                api.updatePet(idToUpdate, pet)
                refreshPets()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Error: Intento de actualizar mascota con ID nulo.")
        }
    }

    // --- Eliminar mascota ---
    // (Esta función no necesita cambios)
    suspend fun delete(pet: Pet) {
        val idToDelete = pet.id
        if (idToDelete != null) {
            try {
                api.deletePet(idToDelete)
                refreshPets()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Error: Intento de borrar mascota con ID nulo.")
        }
    }
}