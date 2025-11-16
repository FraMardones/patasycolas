package com.example.patas_y_colas.ui.screens.menu.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Logout // <--- IMPORTADO
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // <--- IMPORTADO
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.ui.theme.*

@Composable
fun HeaderTopBar(
    onLogoutClicked: () -> Unit,
    userName: String? // <-- RECIBE EL NOMBRE
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp), // Padding del estilo anterior
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- ¡CAMBIO AQUÍ! ---
        // Muestra "Hola, [Nombre]" o "Bienvenido" con el estilo de antes
        Text(
            text = if (userName != null) "Hola, $userName" else "Bienvenido",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Lógica de menú
        Box {
            IconButton(
                onClick = { showMenu = true }
            ) {
                // Icono de "Ajustes" con el estilo de antes
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Ajustes",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Menú desplegable para "Cerrar Sesión"
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color.White)
            ) {
                DropdownMenuItem(
                    text = { Text("Cerrar Sesión", color = PetRed) },
                    onClick = onLogoutClicked,
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = PetRed
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderSection(
    pets: List<Pet>,
    selectedPet: Pet?,
    onPetSelected: (Pet) -> Unit,
    onAddPetClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    userName: String? // <-- RECIBE EL NOMBRE
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
            .background(PetSageGreen) // Color de cabecera
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 24.dp)
        ) {

            // --- ¡CAMBIO AQUÍ! ---
            // Llama a la nueva barra superior fusionada
            HeaderTopBar(
                onLogoutClicked = onLogoutClicked,
                userName = userName
            )

            // Texto "Gestiona a tus mascotas" (del estilo anterior)
            Text(
                text = "Gestiona a tus mascotas",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Selector de mascotas (del estilo anterior)
            PetSelector(
                pets = pets,
                selectedPet = selectedPet,
                onPetSelected = onPetSelected,
                onAddPetClicked = onAddPetClicked
            )
        }
    }
}

// --- NINGÚN CAMBIO DE AQUÍ EN ADELANTE ---
// (Estas funciones son de tu "código anterior" para mantener el estilo)

@Composable
fun PetSelector(
    pets: List<Pet>,
    selectedPet: Pet?,
    onPetSelected: (Pet) -> Unit,
    onAddPetClicked: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        // Lógica de selección corregida para que "Agregar" se deseleccione
        item { AddPetCircle(isSelected = selectedPet == null, onClick = onAddPetClicked) }
        items(pets) { pet -> PetCircle(pet = pet, isSelected = pet == selectedPet, onClick = { onPetSelected(pet) }) }
    }
}

@Composable
fun AddPetCircle(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PetHighlightBlue else Color.White.copy(alpha = 0.2f)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .border(3.dp, borderColor, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Mascota", modifier = Modifier.size(40.dp), tint = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Agregar", color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PetCircle(
    pet: Pet,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (pet.species.lowercase()) {
        "perro" -> Icons.Filled.Pets
        "gato" -> Icons.Filled.Favorite // Puedes cambiarlo por un icono de gato si lo importas
        else -> Icons.Filled.Star
    }
    val borderColor = if (isSelected) PetHighlightBlue else Color.White.copy(alpha = 0.5f)

    // Se agrega 'width(80.dp)' para evitar que nombres largos muevan otros elementos
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(PetBackground) // Fondo para la imagen/icono
                .border(3.dp, borderColor, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (pet.imageUri != null && pet.imageUri.isNotBlank()) { // Verificación extra
                AsyncImage(
                    model = Uri.parse(pet.imageUri),
                    contentDescription = pet.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(imageVector = icon, contentDescription = pet.name, modifier = Modifier.size(40.dp), tint = PetOchre)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = pet.name,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1 // Evita saltos de línea
        )
    }
}