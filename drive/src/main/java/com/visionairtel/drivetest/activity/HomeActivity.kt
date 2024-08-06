package com.visionairtel.drivetest.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.visionairtel.drivetest.presentation.component.Scoped
import com.visionairtel.drivetest.presentation.navigation.AppNavHost
import com.visionairtel.drivetest.presentation.navigation.AppNavigator
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestScreen
import com.visionairtel.drivetest.presentation.screen.main.MainContract
import com.visionairtel.drivetest.presentation.theme.AirtelDriveTestTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            AirtelDriveTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeTop() {
                        finish()
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeTop(onPressedBackButton: () -> Unit) {
    Scaffold(
        topBar = {
            Scoped {
                TopAppBar(
                    title = { Text("Drive Test") },
                    navigationIcon = {
                        IconButton(onClick = {
                            onPressedBackButton()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "back",
                            )
                        }
                    }
                )
            }
        },
    ) {
        DriveTestScreen()
    }

}