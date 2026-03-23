package com.example.autoservice_desktop.features.orders.data

import kotlinx.serialization.Serializable

@Serializable
internal data class OrderDto(
    val id: Long,
    val clientId: Long,
    val carId: Long,
    val masterId: Long? = null,
    val description: String? = null,
    val comment: String? = null,
    val status: String,
    val createdAt: String,
    val plannedCompletionAt: String? = null,
    val completedAt: String? = null,
    val totalAmount: String
)