package com.visionairtel.drivetest.presentation.component

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

@Composable
fun CheckPermissions(isGranted: (Boolean) -> Unit) {
    val context = LocalContext.current
    val permissionState = remember { mutableStateOf(false) }

    val permissions = mutableListOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
    )
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        permissionState.value = permissionsResult.values.all { it }
        isGranted(permissionsResult.values.all { it })
    }

    LaunchedEffect(Unit) {
        val allPermissionsGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PermissionChecker.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            permissionState.value = true
            isGranted(true)
        } else {
            requestPermissionsLauncher.launch(permissions.toTypedArray())
        }
    }
}
