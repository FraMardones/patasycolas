package com.example.patas_y_colas.repository

import android.content.Context
import com.example.patas_y_colas.data.network.LoginRequest
import com.example.patas_y_colas.data.network.RegisterRequest
import com.example.patas_y_colas.data.network.RetrofitClient
import com.example.patas_y_colas.data.network.TokenManager
import com.example.patas_y_colas.model.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow // <--- IMPORTANTE: Asegúrate de importar StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PetRepository(private val context: Context) {

    private val api = RetrofitClient.getClient(context)

    // --- CAMBIO 1: Usamos MutableStateFlow para poder actualizar la lista dinámicamente ---
    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    // --- CORRECCIÓN AQUÍ: Cambiamos Flow por StateFlow ---
    val allPets: StateFlow<List<Pet>> = _pets.asStateFlow()

    // --- CAMBIO 2: Función para recargar la lista desde la API ---
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
                TokenManager.saveToken(context, response.body()!!.token)
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
                TokenManager.saveToken(context, response.body()!!.token)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Crear mascota ---
    suspend fun insert(pet: Pet) {
        try {
            api.createPet(pet)
            // --- CAMBIO 3: Refrescamos la lista automáticamente después de insertar ---
            refreshPets()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- Actualizar mascota ---
    suspend fun update(pet: Pet) {
        val idToUpdate = pet.id
        if (idToUpdate != null) {
            try {
                api.updatePet(idToUpdate, pet)
                // --- CAMBIO 3: Refrescamos la lista automáticamente después de actualizar ---
                refreshPets()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Error: Intento de actualizar mascota con ID nulo.")
        }
    }

    // --- Eliminar mascota ---
    suspend fun delete(pet: Pet) {
        val idToDelete = pet.id
        if (idToDelete != null) {
            try {
                api.deletePet(idToDelete)
                // --- CAMBIO 3: Refrescamos la lista automáticamente después de borrar ---
                refreshPets()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Error: Intento de borrar mascota con ID nulo.")
        }
    }
}