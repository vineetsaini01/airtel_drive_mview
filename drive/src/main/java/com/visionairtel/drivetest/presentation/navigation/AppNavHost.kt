package com.visionairtel.drivetest.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.visionairtel.drivetest.presentation.screen.main.MainScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppRoute.HomeScreen.route,
    ) {

        composable(AppRoute.MainScreen.route) {
            MainScreen()
        }

    }
}