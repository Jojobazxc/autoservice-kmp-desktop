package com.example.autoservice_desktop.features.services.presentation

internal sealed interface ServicesAction {
    data object Load : ServicesAction

    data object OpenCreateDialog : ServicesAction
    data object CloseCreateDialog : ServicesAction
    data class ChangeCreateName(val value: String) : ServicesAction
    data class ChangeCreateDescription(val value: String) : ServicesAction
    data class ChangeCreateBasePrice(val value: String) : ServicesAction
    data class ChangeCreateNormHours(val value: String) : ServicesAction
    data object SubmitCreate : ServicesAction
}