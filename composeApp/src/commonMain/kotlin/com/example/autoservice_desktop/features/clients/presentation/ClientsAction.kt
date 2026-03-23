package com.example.autoservice_desktop.features.clients.presentation

internal sealed interface ClientsAction {
    data object Load : ClientsAction
    data object OpenCreateDialog : ClientsAction
    data object CloseCreateDialog : ClientsAction
    data class UpdateFullName(val value: String) : ClientsAction
    data class UpdatePhone(val value: String) : ClientsAction
    data class UpdateEmail(val value: String) : ClientsAction
    data class UpdateAddress(val value: String) : ClientsAction
    data class UpdateStatus(val value: String) : ClientsAction
    data object SubmitCreate : ClientsAction
}