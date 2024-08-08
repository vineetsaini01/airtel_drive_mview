package com.visionairtel.drivetest.presentation.screen.drive_test

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.visionairtel.drivetest.core.BaseViewModel
import com.visionairtel.drivetest.domain.modal.MarkerItems
import com.visionairtel.drivetest.domain.modal.NetworkDataItems
import com.visionairtel.drivetest.domain.use_case.GetCurrentLocationUseCase
import com.visionairtel.drivetest.domain.use_case.GetMarkerIconUseCase
import com.visionairtel.drivetest.domain.use_case.GetNetworkDataUseCase
import com.visionairtel.drivetest.domain.use_case.SaveCsvFileUseCase
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestContract.*
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestContract.Companion.tag
import com.visionairtel.drivetest.presentation.screen.drive_test.DriveTestContract.DriveEvent.*
import com.visionairtel.drivetest.util.Util.showLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriveTestViewModel @Inject constructor(
    private val saveCsvFileUseCase: SaveCsvFileUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getNetworkDataUseCase: GetNetworkDataUseCase,
    private val getMarkerIconUseCase: GetMarkerIconUseCase,
) : BaseViewModel<DriveEvent, DriveUiEvent>() {

    private var currentLocation: LatLng? = null
    private var initLocation = LatLng(0.0, 0.0)
    private var startRoute = false
    private var networkDataList = emptyList<NetworkDataItems>()

    /** UI State */
    var isLoading by mutableStateOf(true)
        private set
    var isPermissionGranted by mutableStateOf(false)
        private set
    var startButtonText by mutableStateOf("START")
        private set
    var properties by mutableStateOf(MapProperties(isMyLocationEnabled = true))
        private set
    var uiSettings by mutableStateOf(MapUiSettings(scrollGesturesEnabled = true))
        private set
    var markers by mutableStateOf<List<MarkerItems>>(emptyList())
        private set
    var routes by mutableStateOf<List<LatLng>>(emptyList())
        private set
    var cameraPositionState by mutableStateOf(
        CameraPositionState(CameraPosition.fromLatLngZoom(initLocation, 15f))
    )
        private set
    var networkData by mutableStateOf<NetworkDataItems?>(null)
        private set
    var isShowNavigateMarkers by mutableStateOf(true)
        private set

    override fun event(event: DriveEvent) {
        event.apply {
            when (this) {
                DriveCleanButton -> {
                    DriveUiEvent.ShowMsg("Clean Route").triggerEvent()
                    stopRouting()
                }
                DriveSaveButton -> {
                    if (networkDataList.isEmpty()) return
                    DriveUiEvent.Alert(title = "DATA ALERT",
                        msg = "Do you want to save data?",
                        onOkClicked = {
                            viewModelScope.launch(Dispatchers.IO) {
                                val status = saveCsvFileUseCase(networkDataList)
                                if (status) {
                                    DriveUiEvent.ShowMsg("Data is Saved").triggerEvent()
                                    stopRouting()
                                }
                            }
                        },
                        onNoClicked = {}).triggerEvent()
                }
                DriveStartButton -> {
                    if (!startRoute) {
                        startRoute = true
                        startButtonText = "STOP"
                        DriveUiEvent.ShowMsg("Start Route").triggerEvent()
                        return
                    }
                    DriveUiEvent.Alert(title = "DATA ALERT",
                        msg = "Do you want to save data?",
                        onOkClicked = {
                            viewModelScope.launch(Dispatchers.IO) {
                                val status = saveCsvFileUseCase(networkDataList)
                                if (status) {
                                    DriveUiEvent.ShowMsg("Data is saved and route is stop")
                                        .triggerEvent()
                                    stopRouting()
                                }
                            }
                        },
                        onNoClicked = {
                            stopRouting()
                        }).triggerEvent()
                }

                is OnPressedMarkerButton -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
                    viewModelScope.launch(Dispatchers.IO) {
                        networkData = networkDataList[position]
                        isShowNavigateMarkers = false
                        delay(8000L)
                        if (networkData != null) {
                            networkData = null
                            isShowNavigateMarkers = true
                        }

                    }
                }
                OnPressedNetworkInfoCard -> {
                    if (networkData != null) networkData = null
                }
                OnPressedInfoButton -> {
                    isShowNavigateMarkers = !isShowNavigateMarkers
                }
            }
        }
    }

    private fun updateCameraPositionState(latLng: LatLng) {
        cameraPositionState = CameraPositionState(
            position = CameraPosition.fromLatLngZoom(
                latLng, cameraPositionState.position.zoom
            )
        )
    }

    fun updatePermissionGranted(isPermissionGranted: Boolean) {
        this.isPermissionGranted = isPermissionGranted
        if (!isPermissionGranted) return
        startUpdateLocation()
        startUpdateNetworkData()
    }

    private fun startUpdateLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            getCurrentLocationUseCase().onEach {
                onReceiverData(location = it)
            }.launchIn(viewModelScope)
        }
    }

    @SuppressLint("NewApi")
    private fun startUpdateNetworkData() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(5000L)
                if (!startRoute) continue
                onReceiverData(networkData = getNetworkDataUseCase(currentLocation))
            }
        }
    }

    private fun onReceiverData(
        location: Location? = null,
        networkData: NetworkDataItems? = null,
    ) {
        if (location == null && networkData == null) return

        location?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            if (currentLocation == null) {
                updateCameraPositionState(latLng)
                isLoading = false
            }
            if (currentLocation != latLng) {
                currentLocation = latLng
                if (startRoute) {
                    routes = routes + latLng
                    updateCameraPositionState(latLng)
                }
            }
        }
        networkData?.let {
            if (!startRoute) return
            networkDataList = networkDataList + it
            val newMarker = MarkerItems(
                networkType = it.networkTypeEnum.title,
                latLng = it.latLong,
                icon = getMarkerIconUseCase(it.networkTypeEnum)
            )
            markers = markers + newMarker
            showLog(tag, "size of networkDataList--->${networkDataList.size}")
            showLog(tag, "networkDataList data --->${it}")
        }
    }

    private fun stopRouting() {
        networkDataList = emptyList()
        startRoute = false
        routes = emptyList()
        markers = emptyList()
        startButtonText = "START"
    }
}