package com.example.patas_y_colas.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.notifications.NotificationScheduler
import com.example.patas_y_colas.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel(
    private val repository: PetRepository,
    private val application: Application
) : ViewModel() {

    // --- (Esto está bien) ---
    val allPets: StateFlow<List<Pet>> = repository.allPets

    private val _catFact = MutableStateFlow<String?>(null)
    val catFact = _catFact.asStateFlow()
    // ------------------------------------------

    // --- ¡CORREGIDO! ---
    // Tu repositorio usa 'refreshPets()'
    init {
        viewModelScope.launch {
            repository.refreshPets() // <-- CORREGIDO
        }
    }

    // --- ¡CORREGIDO! ---
    // Tu repositorio usa 'insert(pet)'
    fun insert(pet: Pet) = viewModelScope.launch {
        repository.insert(pet) // <-- CORREGIDO

        // El resto de tu lógica de notificaciones
        NotificationScheduler.scheduleNotifications(application, pet)
        pet.vaccines.lastOrNull()?.vaccineName?.let { vaccineName ->
            if(vaccineName.isNotBlank()) {
                NotificationScheduler.sendTestNotification(application, pet.name, vaccineName)
            }
        }
    }

    // --- ¡CORREGIDO! ---
    // Tu repositorio usa 'update(pet)'
    fun update(pet: Pet) = viewModelScope.launch {
        repository.update(pet) // <-- CORREGIDO

        // El resto de tu lógica de notificaciones
        NotificationScheduler.scheduleNotifications(application, pet)
        pet.vaccines.lastOrNull()?.vaccineName?.let { vaccineName ->
            if(vaccineName.isNotBlank()) {
                NotificationScheduler.sendTestNotification(application, pet.name, vaccineName)
            }
        }
    }

    // --- ¡CORREGIDO! ---
    // Tu repositorio usa 'delete(pet)'
    fun delete(pet: Pet) = viewModelScope.launch {
        NotificationScheduler.cancelNotificationsForPet(application, pet)
        repository.delete(pet) // <-- CORREGIDO
    }

    // --- (Esto está bien) ---
    fun loadFunFact() {
        viewModelScope.launch {
            _catFact.value = "Cargando..."
            _catFact.value = repository.getFunFact()
        }
    }

    fun clearFunFact() {
        _catFact.value = null
    }
    // -----------------------------------------
}

// --- (La Factory está bien) ---
class MenuViewModelFactory(
    private val repository: PetRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}