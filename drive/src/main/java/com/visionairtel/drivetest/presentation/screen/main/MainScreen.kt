package com.visionairtel.drivetest.presentation.screen.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.visionairtel.drivetest.presentation.component.Scoped
import com.visionairtel.drivetest.presentation.component.HSpacer
import com.visionairtel.drivetest.presentation.navigation.MainNavHost
import com.visionairtel.drivetest.presentation.screen.main.MainContract.ClickedEvent.*
import com.visionairtel.drivetest.presentation.screen.main.MainContract.Companion
import com.visionairtel.drivetest.presentation.screen.main.MainContract.UIEvent.CloseDrawer
import com.visionairtel.drivetest.presentation.screen.main.MainContract.UIEvent.OpenDrawer

@Composable
fun MainScreen(vm: MainViewModel = hiltViewModel()) {

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val navController = rememberNavController().apply {
        setLifecycleOwner(LocalLifecycleOwner.current)
    }

    LaunchedEffect(Unit) {
        vm.initVM { navController }
    }

    LaunchedEffect(vm.uiEvents) {
        vm.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                CloseDrawer -> scaffoldState.drawerState.close()
                OpenDrawer -> scaffoldState.drawerState.open()
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        topBar = {
            Scoped {
                TopAppBar(
                    title = { Text(text = vm.title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            vm.event(ClickedDrawer)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "drawer_icon",
                            )
                        }
                    }
                )
            }
        },
        drawerContent = {

            10.HSpacer()
//            Image(
//                modifier = Modifier
//                    .width(70.dp)
//                    .padding(horizontal = 16.dp),
//                painter = painterResource(id = R.drawable.app_icon),
//                contentDescription = "app_icon"
//            )
            5.HSpacer()
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Airtel Drive (${vm.version})",
            )

            30.HSpacer()

            Companion.drawerTitles.forEach {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                        .clickable {
                            it.event?.let { event ->
                                vm.event(event)
                            }
                        },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    10.HSpacer()
                    Text(modifier = Modifier.padding(14.dp), text = it.title)
                    10.HSpacer()
                }
            }

        },
    ) {

        MainNavHost(
            Modifier.padding(it),
            navController = navController
        )
    }

}
