package com.visionairtel.drivetest.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestScreen
import com.visionairtel.drivetest.presentation.screen.home.HomeScreen

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppRoute.HomeScreen.route,
    ) {


        composable(AppRoute.HomeScreen.route) {
            HomeScreen()
        }

        composable(AppRoute.DriveTestScreen.route) {
            DriveTestScreen()
        }

    }
}