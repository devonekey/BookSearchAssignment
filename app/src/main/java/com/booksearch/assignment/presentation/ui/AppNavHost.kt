package com.booksearch.assignment.presentation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.booksearch.assignment.presentation.ui.screen.MainScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable(route = "main") {
            MainScreen(navController = navController)
        }
        composable(route = "detail/{isbn}") { backStack -> }
    }
}
