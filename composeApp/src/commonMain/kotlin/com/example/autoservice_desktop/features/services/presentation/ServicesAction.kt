package com.example.autoservice_desktop.features.services.presentation

internal sealed interface ServicesAction {
    data object Load : ServicesAction
}