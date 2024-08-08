package com.visionairtel.drivetest.presentation.component

import android.Manifest.permission
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

@Composable
fun CheckPermissions(isGranted: (Boolean) -> Unit) {
    val context = LocalContext.current
    var isShowForegroundPermissionsAlert by remember { mutableStateOf(false) }
    var isShowBackgroundPermissionsAlert by remember { mutableStateOf(false) }

    val foregroundPermissions = mutableListOf(
        permission.ACCESS_COARSE_LOCATION,
        permission.ACCESS_FINE_LOCATION,
        permission.READ_PHONE_STATE,
        permission.ACCESS_NETWORK_STATE,
    )
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        foregroundPermissions.add(permission.WRITE_EXTERNAL_STORAGE)
        foregroundPermissions.add(permission.READ_EXTERNAL_STORAGE)
    }

    val backgroundPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        mutableListOf(permission.ACCESS_BACKGROUND_LOCATION) else emptyList()


    val backgroundPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        if (permissionsResult.values.all { it }) isGranted(true)
    }

    val foregroundPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val isForegroundPermissionsGranted = permissionsResult.values.all { it }
        if (isForegroundPermissionsGranted) {
            if (backgroundPermissions.isEmpty()) {
                isGranted(true)
            }
            isShowBackgroundPermissionsAlert = true
        } else {
            isShowForegroundPermissionsAlert = true
        }
    }



    LaunchedEffect(Unit) {
        val allForegroundPermissionsGranted = foregroundPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context, permission
            ) == PermissionChecker.PERMISSION_GRANTED
        }

        val allBackgroundPermissionsGranted =
            if (backgroundPermissions.isEmpty()) true else backgroundPermissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    context, permission
                ) == PermissionChecker.PERMISSION_GRANTED
            }

        if (allForegroundPermissionsGranted) {
            if (allBackgroundPermissionsGranted) isGranted(true)
            else isShowBackgroundPermissionsAlert = true
        } else {
            foregroundPermissionsLauncher.launch(foregroundPermissions.toTypedArray())
        }
    }

    if (isShowBackgroundPermissionsAlert) {
        AlertDialog(onDismissRequest = { /* Dismiss dialog */ },
            title = { Text("Permission Required") },
            text = {
                Text("This app needs access to your location in the background to provide accurate location-based features even when the app is not in use. set location permission at Allow all the time")
            },
            confirmButton = {
                Button(onClick = {
                    backgroundPermissionsLauncher.launch(backgroundPermissions.toTypedArray())
                }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                Button(onClick = {
                    isGranted(true)
                }) {
                    Text("Deny")
                }
            })
    }

    if (isShowForegroundPermissionsAlert) {
        AlertDialog(
            onDismissRequest = { /* Dismiss dialog */ },
            title = { Text("Permission Required") },
            text = {
                Text("This app needs access to some permission")
            },
            confirmButton = {
                Button(onClick = {
                    isShowForegroundPermissionsAlert = false
                    foregroundPermissionsLauncher.launch(foregroundPermissions.toTypedArray())
                }) {
                    Text("Continue")
                }
            },
        )
    }
}
