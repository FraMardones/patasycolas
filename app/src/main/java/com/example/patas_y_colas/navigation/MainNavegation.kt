package com.example.patas_y_colas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.patas_y_colas.ui.theme.screens.LoginScreen
import com.example.patas_y_colas.ui.theme.screens.MenuScreen
import com.example.patas_y_colas.ui.theme.screens.RegisterScreen

@Composable
fun MainNavigation() {
    // Aseguramos que el navController se cree aquí
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login_screen") {
        composable("login_screen") {
            LoginScreen(navController = navController)
        }
        composable("register_screen") {
            RegisterScreen(navController = navController)
        }
        composable("menu_screen") {
            MenuScreen(navController = navController)
        }
    }
}