package com.example.autoservice_desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import autoservice_desktop.composeapp.generated.resources.Res
import autoservice_desktop.composeapp.generated.resources.app_icon
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Auto Service CRM",
        icon = painterResource(Res.drawable.app_icon),
    ) {
        App()
    }
}