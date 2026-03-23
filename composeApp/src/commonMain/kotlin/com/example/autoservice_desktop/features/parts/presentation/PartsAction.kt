package com.example.autoservice_desktop.features.parts.presentation

internal sealed interface PartsAction {
    data object Load : PartsAction
}