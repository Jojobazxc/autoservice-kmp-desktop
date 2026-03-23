package com.example.autoservice_desktop.features.clients.presentation

import com.example.autoservice_desktop.features.clients.data.ClientDto

internal data class ClientsState(
    val items: List<ClientDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    val isCreateDialogOpen: Boolean = false,
    val isCreating: Boolean = false,

    val fullNameInput: String = "",
    val phoneInput: String = "",
    val emailInput: String = "",
    val addressInput: String = "",
    val statusInput: String = "REGULAR",

    val createError: String? = null
)