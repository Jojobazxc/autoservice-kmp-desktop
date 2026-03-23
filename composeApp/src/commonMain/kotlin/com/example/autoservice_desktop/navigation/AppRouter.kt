package com.example.autoservice_desktop.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class AppRouter {
    var currentScreen: Screen by mutableStateOf(Screen.Clients)
        private set

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }
}