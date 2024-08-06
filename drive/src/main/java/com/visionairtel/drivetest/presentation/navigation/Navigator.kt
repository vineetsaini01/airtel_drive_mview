package com.visionairtel.drivetest.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

open class Navigator {

    /**
     * Make sure setNavController is called before access these all properties
     */

    private var navControllerRef: WeakReference<NavHostController>? = null
    val navController: NavHostController?
        get() = navControllerRef?.get()

    var isInitialized = false
        private set

    val currentDestination: String?
        get() = navController?.currentDestination?.route

    fun setNavController(getNavController: () -> NavHostController) {
        if (isInitialized) return
        navControllerRef = WeakReference(getNavController())
        isInitialized = true
    }


    fun popBackStack() = navController?.popBackStack()

    fun popBackStack(route: AppRoute, inclusive: Boolean = false) {
        navControllerRef?.get()?.popBackStack(route = route.route, inclusive = inclusive)
    }

    fun getDestination() = callbackFlow {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            trySend(destination)
        }
        navControllerRef?.get()?.addOnDestinationChangedListener(listener)

        awaitClose {
            navControllerRef?.get()?.removeOnDestinationChangedListener(listener)
        }
    }


    private fun navigate(route: AppRoute) {
        navControllerRef?.get()?.navigate(route.route) {
            launchSingleTop = true
        }
    }

    fun navigateWithCheckCurrentDestination(route: AppRoute) {
        if (currentDestination != route.route) navigate(route)
    }


}


@Singleton
class AppNavigator @Inject constructor() : Navigator()

@Singleton
class MainNavigator @Inject constructor() : Navigator()