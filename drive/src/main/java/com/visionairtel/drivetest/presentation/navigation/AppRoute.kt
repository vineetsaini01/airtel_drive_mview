package com.visionairtel.drivetest.presentation.navigation

sealed class AppRoute(val route: String) {

    object MainScreen : AppRoute("main_screen")
    object WifiCoverageTestScreen : AppRoute("wifi_coverage_test_screen")
    object TraceRouteTestScreen : AppRoute("TraceRouteTestScreen")
    object HomeScreen : AppRoute("home_screen")
    object DriveTestScreen : AppRoute("drive_test_screen")
}