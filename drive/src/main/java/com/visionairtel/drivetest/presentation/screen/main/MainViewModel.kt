package com.visionairtel.drivetest.presentation.screen.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.visionairtel.drivetest.core.BaseViewModel
import com.visionairtel.drivetest.domain.use_case.GetVersionUseCase
import com.visionairtel.drivetest.presentation.navigation.AppRoute
import com.visionairtel.drivetest.presentation.navigation.MainNavigator
import com.visionairtel.drivetest.presentation.screen.main.MainContract.ClickedEvent
import com.visionairtel.drivetest.presentation.screen.main.MainContract.ClickedEvent.*
import com.visionairtel.drivetest.presentation.screen.main.MainContract.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainNavigator: MainNavigator,
    private val getVersionUseCase: GetVersionUseCase,
) : BaseViewModel<ClickedEvent, UIEvent>() {

    var title by mutableStateOf("Home")
        private set
    val version: String get() = getVersionUseCase()

    fun initVM(getNavHostController: () -> NavHostController) {
        mainNavigator.apply {
            if (isInitialized) return

            setNavController {
                getNavHostController()
            }

            getDestination().onEach {
                title = when (it.route) {
                    AppRoute.HomeScreen.route -> "Home"
                    AppRoute.DriveTestScreen.route -> "Drive Text"
                    else -> "Unknown"
                }
            }.launchIn(viewModelScope)
        }
    }


    override fun event(event: ClickedEvent) {
        event.apply {
            when (this) {
                ClickedDriveTest -> {
                    mainNavigator.navigateWithCheckCurrentDestination(AppRoute.DriveTestScreen)
                    UIEvent.CloseDrawer.triggerEvent()
                }
                ClickedHome -> {
                    mainNavigator.popBackStack()
                    mainNavigator.navigateWithCheckCurrentDestination(AppRoute.HomeScreen)
                    UIEvent.CloseDrawer.triggerEvent()
                }
                ClickedDrawer -> {
                    UIEvent.OpenDrawer.triggerEvent()
                }
            }
        }
    }

}