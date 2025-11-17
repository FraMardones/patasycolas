package com.example.patas_y_colas.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.patas_y_colas.data.network.LoginRequest
import com.example.patas_y_colas.data.network.RegisterRequest
import com.example.patas_y_colas.data.network.RetrofitClient
import com.example.patas_y_colas.data.network.TokenManager
import com.example.patas_y_colas.model.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class PetRepository(private val context: Context) {

    private val api = RetrofitClient.getClient(context)

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val allPets: StateFlow<List<Pet>> = _pets.asStateFlow()


    suspend fun getFunFact(): String? {
        return try {
            api.getCatFact().fact
        } catch (e: Exception) {
            e.printStackTrace()
            "No se pudo cargar el dato curioso. Revisa tu conexión."
        }
    }
    suspend fun refreshPets() {
        try {
            val petsFromApi = api.getAllPets()
            _pets.value = petsFromApi
        } catch (e: Exception) {
            e.printStackTrace()
            _pets.value = emptyList()
        }
    }

    // --- Login (Sin cambios) ---
    suspend fun login(email: String, pass: String): Boolean {
        return try {
            val response = api.login(LoginRequest(email, pass))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                TokenManager.saveTokens(
                    context,
                    authResponse.token,
                    authResponse.refreshToken,
                    authResponse.firstname
                )
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Registro (Sin cambios) ---
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
                val authResponse = response.body()!!
                TokenManager.saveTokens(
                    context,
                    authResponse.token,
                    authResponse.refreshToken,
                    authResponse.firstname
                )
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Logout (Sin cambios) ---
    fun logout() {
        TokenManager.clearTokens(context)
        _pets.value = emptyList()
    }


    // --- ¡CORREGIDO! ---
    suspend fun insert(pet: Pet) {
        try {
            // 1. Crear partes de texto
            val nameRB = pet.name.toRequestBody("text/plain".toMediaTypeOrNull())
            val speciesRB = pet.species.toRequestBody("text/plain".toMediaTypeOrNull())
            val breedRB = pet.breed.toRequestBody("text/plain".toMediaTypeOrNull())
            val ageRB = pet.age.toRequestBody("text/plain".toMediaTypeOrNull())
            val weightRB = pet.weight.toRequestBody("text/plain".toMediaTypeOrNull())

            // 2. Crear parte de imagen (con null-check)
            if (pet.imageUri == null) {
                Log.e("PetRepository", "Error: Intento de insertar mascota SIN imageUri.")
                return // No se puede continuar sin imagen
            }
            val imageUri = Uri.parse(pet.imageUri) // Ahora es seguro
            val imageFile = getFileFromUri(imageUri)
            val imagePart = MultipartBody.Part.createFormData("imageFile", imageFile.name, imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull()))

            // 3. Llamar a la API
            api.createPet(nameRB, speciesRB, breedRB, ageRB, weightRB, imagePart)

            refreshPets()
        } catch (e: Exception) {
            Log.e("PetRepository", "Error al INSERTAR mascota", e)
        }
    }

    // --- ¡CORREGIDO! ---
    suspend fun update(pet: Pet) {
        val idToUpdate = pet.id
        if (idToUpdate != null) {
            try {
                // 1. Crear partes de texto
                val nameRB = pet.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val speciesRB = pet.species.toRequestBody("text/plain".toMediaTypeOrNull())
                val breedRB = pet.breed.toRequestBody("text/plain".toMediaTypeOrNull())
                val ageRB = pet.age.toRequestBody("text/plain".toMediaTypeOrNull())
                val weightRB = pet.weight.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null

                // --- ¡ESTA ES TU LÍNEA CORREGIDA! ---
                // Usamos un safe call (?.) y comparamos con 'true'
                if (pet.imageUri?.startsWith("content://") == true) {
                    val imageUri = Uri.parse(pet.imageUri) // Ahora es seguro
                    val imageFile = getFileFromUri(imageUri)
                    imagePart = MultipartBody.Part.createFormData("imageFile", imageFile.name, imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull()))
                }

                // 3. Llamar a la API
                api.updatePet(idToUpdate, nameRB, speciesRB, breedRB, ageRB, weightRB, imagePart)

                refreshPets()
            } catch (e: Exception) {
                Log.e("PetRepository", "Error al ACTUALIZAR mascota", e)
            }
        } else {
            Log.e("PetRepository", "Error: Intento de actualizar mascota con ID nulo.")
        }
    }

    // --- ¡CORREGIDO! ---
    suspend fun delete(pet: Pet) {
        val idToDelete = pet.id
        if (idToDelete != null) {
            try {
                api.deletePet(idToDelete)
                refreshPets()
            } catch (e: Exception) {
                Log.e("PetRepository", "Error al BORRAR mascota", e)
            }
        } else {
            Log.e("PetRepository", "Error: Intento de borrar mascota con ID nulo.")
        }
    }

    // --- Helper (Sin cambios) ---
    private fun getFileFromUri(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return file
    }
}