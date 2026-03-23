package com.example.autoservice_desktop.features.cars.presentation

internal data class CarsState(
    val items: List<CarListItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)