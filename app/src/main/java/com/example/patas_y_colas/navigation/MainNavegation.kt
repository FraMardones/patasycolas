package com.example.patas_y_colas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.patas_y_colas.ui.theme.screens.MenuScreen
import com.example.patas_y_colas.ui.theme.screens.PortadaScreen // Asumo que esta es tu portada
import com.example.patas_y_colas.ui.theme.screens.LoginScreen
import com.example.patas_y_colas.ui.theme.screens.RegisterScreen // Importa la nueva pantalla

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    // Definimos el flujo: Portada -> Login <-> Register -> Menu
    NavHost(navController = navController, startDestination = "portada") {

        composable("portada") {
            // Al dar "Continuar" en la portada, vamos al Login
            PortadaScreen(onContinueClick = { navController.navigate("login") })
        }

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("menu") {
            MenuScreen()
        }
    }
}