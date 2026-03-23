package com.example.autoservice_desktop.features.cars.data

import kotlinx.serialization.Serializable

@Serializable
internal data class CarDto(
    val id: Long,
    val clientId: Long,
    val brand: String,
    val model: String,
    val year: Int? = null,
    val plateNumber: String,
    val vin: String? = null,
    val mileage: Int? = null
)