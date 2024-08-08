package com.visionairtel.drivetest.presentation.screen.drive_test

import com.visionairtel.drivetest.util.Util.NetworkTypeEnum.*

class DriveTestContract {
    companion object {
        const val tag = "drive"
        val navigateMarkers = listOf(GSM, WCDMA, LTE, NS)
    }

    sealed class DriveEvent {
        object DriveStartButton : DriveEvent()
        object DriveSaveButton : DriveEvent()
        object DriveCleanButton : DriveEvent()
        object OnPressedInfoButton : DriveEvent()
        object OnPressedNetworkInfoCard : DriveEvent()
        data class OnPressedMarkerButton(val position: Int) : DriveEvent()
    }

    sealed class DriveUiEvent {
        data class ShowMsg(val msg: String) : DriveUiEvent()
        data class Alert(
            val title: String,
            val msg: String,
            val onOkClicked: () -> Unit,
            val onNoClicked: () -> Unit,
        ) : DriveUiEvent()
    }
}