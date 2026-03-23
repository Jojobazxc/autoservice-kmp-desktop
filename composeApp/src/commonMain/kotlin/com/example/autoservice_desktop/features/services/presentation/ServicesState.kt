package com.example.autoservice_desktop.features.services.presentation

import com.example.autoservice_desktop.features.services.data.ServiceDto

internal data class ServicesState(
    val items: List<ServiceDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)