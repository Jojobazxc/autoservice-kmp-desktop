package com.example.autoservice_desktop.features.orders.data

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateOrderRequest(
    val clientId: Long,
    val carId: Long,
    val masterId: Long? = null,
    val description: String? = null,
    val comment: String? = null,
    val status: String = "CREATED",
    val plannedCompletionAt: String? = null
)