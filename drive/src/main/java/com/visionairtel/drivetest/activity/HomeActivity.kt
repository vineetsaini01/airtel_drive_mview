package com.visionairtel.drivetest.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.visionairtel.drivetest.presentation.navigation.AppNavHost
import com.visionairtel.drivetest.presentation.navigation.AppNavigator
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestScreen
import com.visionairtel.drivetest.presentation.theme.AirtelDriveTestTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    @Inject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            AirtelDriveTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DriveTestScreen()
                }
            }
        }
    }
}