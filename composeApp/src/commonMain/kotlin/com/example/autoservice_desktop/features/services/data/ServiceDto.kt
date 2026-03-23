package com.example.autoservice_desktop.features.services.data

import kotlinx.serialization.Serializable

@Serializable
internal data class ServiceDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    val basePrice: String,
    val normHours: String? = null
)