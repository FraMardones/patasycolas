package com.example.patas_y_colas

import android.app.Application
import com.example.patas_y_colas.repository.PetRepository

class PetAplication : Application() {

    val repository by lazy {
        PetRepository(this)
    }
}