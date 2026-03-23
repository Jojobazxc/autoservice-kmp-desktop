package com.example.autoservice_desktop.features.cars.presentation

internal data class CarsState(
    val items: List<CarListItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    val isCreateDialogOpen: Boolean = false,
    val createForm: CreateCarForm = CreateCarForm(),
    val clientOptions: List<CarReferenceOptionUi> = emptyList()
)

internal data class CreateCarForm(
    val clientId: Long? = null,
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val plateNumber: String = "",
    val vin: String = "",
    val mileage: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

internal data class CarReferenceOptionUi(
    val id: Long,
    val title: String
)