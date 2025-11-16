package com.example.patas_y_colas.ui.screens.menu.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
// --- IMPORTS AÑADIDOS ---
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.runtime.*
// --- FIN DE IMPORTS AÑADIDOS ---
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.patas_y_colas.R // Asumiendo que R.drawable.gato existe
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.ui.theme.PetRed
import com.example.patas_y_colas.ui.theme.PetTextDark

@Composable
// --- AÑADIDO: onLogoutClicked ---
fun HeaderTopBar(onAddPetClicked: () -> Unit, onLogoutClicked: () -> Unit) {
    // --- AÑADIDO: Estado para el menú ---
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Mis Mascotas", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PetTextDark)

        // Contenedor para el ícono y el menú
        Box {
            // Botón de Configuración (Tuerca)
            IconButton(
                // --- CAMBIO: Mostrar el menú al hacer clic ---
                onClick = { showMenu = true }
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = PetTextDark)
            }

            // --- AÑADIDO: El menú desplegable ---
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color.White)
            ) {
                DropdownMenuItem(
                    text = { Text("Cerrar Sesión", color = PetRed) },
                    onClick = onLogoutClicked, // Llama a la función de logout
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = PetRed
                        )
                    }
                )
                // (Aquí podrías añadir más opciones en el futuro)
            }
            // --- FIN DEL BLOQUE AÑADIDO ---
        }
    }
}

@Composable
fun HeaderSection(
    pets: List<Pet>,
    selectedPet: Pet?,
    onPetSelected: (Pet) -> Unit,
    onAddPetClicked: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderTopBar(
            onAddPetClicked = onAddPetClicked,
            // --- PASAR LA FUNCIÓN AL TOP BAR ---
            onLogoutClicked = onLogoutClicked
        )
        Spacer(modifier = Modifier.height(24.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            items(pets) { pet ->
                PetAvatar(
                    pet = pet,
                    isSelected = pet == selectedPet,
                    onPetSelected = { onPetSelected(pet) }
                )
            }
            item {
                AddPetButton(onClick = onAddPetClicked)
            }
        }
    }
}

@Composable
fun PetAvatar(pet: Pet, isSelected: Boolean, onPetSelected: () -> Unit) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    // TODO: Usar la imagen real (pet.imageUri) o una imagen por defecto
    val imageRes = if (pet.species == "Gato") R.drawable.gato else R.drawable.gato // (Placeholder)

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = pet.name,
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .border(4.dp, borderColor, CircleShape)
            .clickable(onClick = onPetSelected),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun AddPetButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Add, contentDescription = "Añadir Mascota", tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}