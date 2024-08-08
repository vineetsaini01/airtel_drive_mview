package com.visionairtel.drivetest.presentation.screen.drive_test

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.*
import com.visionairtel.drivetest.R
import com.visionairtel.drivetest.domain.modal.NetworkDataItems
import com.visionairtel.drivetest.presentation.component.*
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestContract.Companion.tag
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestContract.DriveEvent.*
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestContract.DriveUiEvent.Alert
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestContract.DriveUiEvent.ShowMsg
import com.visionairtel.drivetest.util.Util.showAlert
import com.visionairtel.drivetest.util.Util.showLog
import com.visionairtel.drivetest.util.Util.showToast
import kotlinx.coroutines.delay

@Composable
fun DriveTestScreen(vm: DriveTestViewModel = hiltViewModel()) {
    val context: Context = LocalContext.current

    if (!vm.isPermissionGranted) {
        CheckPermissions(vm::updatePermissionGranted)
        return
    }
    if (vm.isLoading) {
        PleaseWait()
        return
    }

    LaunchedEffect(vm.uiEvents) {
        vm.uiEvents.collect { event ->
            when (event) {
                is ShowMsg -> context.showToast(event.msg)
                is Alert -> context.showAlert(title = event.title,
                    msg = event.msg,
                    onPressedPositiveButton = {
                        it.dismiss()
                        event.onOkClicked()
                    },
                    onPressedNegativeButton = {
                        it.dismiss()
                        event.onNoClicked()
                    }
                )
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(8.5f)
        ) {
            Scoped {
                GoogleMap(
                    cameraPositionState = vm.cameraPositionState,
                    properties = vm.properties,
                    uiSettings = vm.uiSettings,
                ) {
                    showLog(tag, "GoogleMap refresh")
                    Scoped {
                        showLog(tag, "markers refresh")
                        repeat(vm.markers.size) { position ->
                            val marker = vm.markers[position]
                            Marker(
                                state = MarkerState(position = marker.latLng),
                                icon = marker.icon,
                                onClick = {
                                    vm.event(OnPressedMarkerButton(position))
                                    true
                                }
                            )
                        }
                    }
                    Scoped {
                        showLog(tag, "Polyline refresh")
                        Polyline(
                            points = vm.routes, clickable = true, color = Color.Blue, width = 8f
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 60.dp)
            ) {

                Scoped {
                    AnimatedVisibility(
                        visible = vm.isShowNavigateMarkers,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)

                            ) {
                                showLog(tag, "icon refresh")
                                DriveTestContract.navigateMarkers.forEach {
                                    Row(modifier = Modifier.weight(1f)) {
                                        Icon(
                                            modifier = Modifier.size(24.dp),
                                            painter = painterResource(id = R.drawable.google_map_marker),
                                            contentDescription = "google map marker",
                                            tint = it.color
                                        )
                                        Text(text = it.title)
                                    }
                                }
                            }
                        }
                    }
                }

                Scoped {
                    AnimatedVisibility(
                        visible = vm.networkData != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        BlinkingBorderCard(networkData = vm.networkData) {
                            vm.event(OnPressedNetworkInfoCard)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .weight(1.1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)

        ) {
            showLog(tag, "buttons refresh")
            Scoped {
                CustomButton(modifier = Modifier.weight(1f), title = vm.startButtonText, onClick = {
                    vm.event(DriveStartButton)
                })
            }
            CustomButton(modifier = Modifier.weight(1f), title = "SAVE", onClick = {
                vm.event(DriveSaveButton)
            })
            CustomButton(modifier = Modifier.weight(1f), title = "CLEAR", onClick = {
                vm.event(DriveCleanButton)
            })
            CustomButton(modifier = Modifier.weight(1f), title = "DETAILS", onClick = {
                vm.event(OnPressedInfoButton)
            })
        }
    }
}

@Composable
private fun BlinkingBorderCard(networkData: NetworkDataItems?, onClick: () -> Unit) {
    var isRed by remember { mutableStateOf(true) }

    val borderColor by animateColorAsState(
        targetValue = if (isRed) Color.Red else Color.Transparent,
        animationSpec = tween(durationMillis = 500)
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            isRed = !isRed
        }
    }

    Scoped {
        Card(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, borderColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable { onClick() }
        ) {
            networkData?.let {
                Column(modifier = Modifier.padding(14.dp)) {
                    TextKeyValue("Latitude", it.latLong.latitude.toString())
                    TextKeyValue("Longitude", it.latLong.longitude.toString())
                    TextKeyValue("Network Type", it.networkType)
                    TextKeyValue("Signal Quality", it.signalQuality)
                    TextKeyValue("Cell Id", it.cellID)
                    TextKeyValue("MCC", it.mcc)
                    TextKeyValue("MNC", it.mnc)
                    TextKeyValue("RSSI", it.rssi)
                    TextKeyValue("RSSI DBM", it.rssiDbm, "Dbm")
                }
            }
        }
    }


}


@Composable
private fun TextKeyValue(
    key: String,
    value: String,
    postString: String = "",
) {
    if (value.isNotEmpty()) {
        Text(text = "$key: $value$postString")
    }
}