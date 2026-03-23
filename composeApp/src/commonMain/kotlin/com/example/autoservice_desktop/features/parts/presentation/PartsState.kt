package com.example.autoservice_desktop.features.parts.presentation

import com.example.autoservice_desktop.features.parts.data.PartDto

internal data class PartsState(
    val items: List<PartDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)