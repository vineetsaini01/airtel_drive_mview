package com.visionairtel.drivetest.presentation.screen.main

import com.visionairtel.drivetest.presentation.screen.main.MainContract.ClickedEvent.ClickedDriveTest
import com.visionairtel.drivetest.presentation.screen.main.MainContract.ClickedEvent.ClickedHome

class MainContract {

    companion object {
        data class DrawerTitlesItem(
            val title: String,
            val event: ClickedEvent? = null,
        )

        val drawerTitles = listOf(
            DrawerTitlesItem(title = "Home", ClickedHome),
            DrawerTitlesItem(title = "Ping"),
            DrawerTitlesItem(title = "Traceroute"),
            DrawerTitlesItem(title = "Create Wifi Coverage Map"),
            DrawerTitlesItem(title = "Surveys List"),
            DrawerTitlesItem(title = "Drive Test", ClickedDriveTest),
            DrawerTitlesItem(title = "Performance Test"),
            DrawerTitlesItem(title = "Setting"),
        )
    }

    sealed class ClickedEvent {
        object ClickedDriveTest : ClickedEvent()
        object ClickedHome : ClickedEvent()
        object ClickedDrawer : ClickedEvent()
    }

    sealed class UIEvent  {
        object OpenDrawer : UIEvent()
        object CloseDrawer : UIEvent()
    }
}
