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
import androidx.compose.material.icons.outlined.Logout // <--- Importado para el menú
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
import coil.compose.AsyncImage
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.ui.theme.*

/**
 * Esta es la nueva barra superior que combina la lógica del menú
 * con el estilo visual que querías (texto "Bienvenido" y color blanco).
 */
@Composable
fun HeaderTopBar(onLogoutClicked: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Texto del "código anterior"
        Text(text = "Bienvenido", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)

        // Lógica de menú del "código nuevo"
        Box {
            // Botón de Configuración (Tuerca)
            IconButton(
                onClick = { showMenu = true }
            ) {
                // Icono con el estilo del "código anterior"
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Ajustes",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Menú desplegable del "código nuevo"
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
            }
        }
    }
}

/**
 * Esta es la sección del cabezal principal, con el fondo verde.
 * Ahora incluye 'onLogoutClicked' y llama al 'HeaderTopBar' actualizado.
 */
@Composable
fun HeaderSection(
    pets: List<Pet>,
    selectedPet: Pet?,
    onPetSelected: (Pet) -> Unit,
    onAddPetClicked: () -> Unit,
    onLogoutClicked: () -> Unit // <-- Parámetro nuevo
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
            // Llamamos a la nueva barra superior fusionada
            HeaderTopBar(onLogoutClicked = onLogoutClicked)

            // Mantenemos el subtítulo del "código anterior"
            Text(text = "Gestiona a tus mascotas", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))

            // Mantenemos el PetSelector del "código anterior"
            PetSelector(
                pets = pets,
                selectedPet = selectedPet,
                onPetSelected = onPetSelected,
                onAddPetClicked = onAddPetClicked
            )
        }
    }
}

/**
 * El resto de funciones se mantienen idénticas a tu "código anterior"
 * para preservar el estilo visual.
 */
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
        // Se cambia el 'selectedPet == null' para que coincida con la lógica
        // de tu primer archivo (el botón "Add" no está "seleccionado" si una mascota lo está)
        item { AddPetCircle(isSelected = selectedPet == null && pets.isNotEmpty(), onClick = onAddPetClicked) }
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

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) { // Añadido para centrar texto
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(PetBackground) // Fondo para la imagen/icono
                .border(3.dp, borderColor, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (pet.imageUri != null) {
                AsyncImage(model = Uri.parse(pet.imageUri), contentDescription = pet.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Icon(imageVector = icon, contentDescription = pet.name, modifier = Modifier.size(40.dp), tint = PetOchre)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = pet.name,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center, // Asegura que el nombre esté centrado
            maxLines = 1 // Evita que nombres largos se corten mal
        )
    }
}