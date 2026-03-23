package com.example.autoservice_desktop.features.services.data

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateServiceRequest(
    val name: String,
    val description: String? = null,
    val basePrice: String,
    val normHours: String? = null
)