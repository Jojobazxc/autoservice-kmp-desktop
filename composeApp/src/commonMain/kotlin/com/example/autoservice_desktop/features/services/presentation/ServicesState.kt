package com.example.autoservice_desktop.features.services.presentation

import com.example.autoservice_desktop.features.services.data.ServiceDto

internal data class ServicesState(
    val items: List<ServiceDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    val isCreateDialogOpen: Boolean = false,
    val createForm: CreateServiceForm = CreateServiceForm()
)

internal data class CreateServiceForm(
    val name: String = "",
    val description: String = "",
    val basePrice: String = "",
    val normHours: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)