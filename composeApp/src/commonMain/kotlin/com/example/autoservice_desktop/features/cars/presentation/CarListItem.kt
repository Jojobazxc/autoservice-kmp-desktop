package com.example.autoservice_desktop.features.cars.presentation

internal data class CarListItem(
    val id: Long,
    val ownerName: String,
    val brand: String,
    val model: String,
    val year: Int?,
    val plateNumber: String,
    val vin: String?,
    val mileage: Int?
)