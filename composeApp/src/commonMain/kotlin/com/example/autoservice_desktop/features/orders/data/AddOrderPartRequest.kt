package com.example.autoservice_desktop.features.orders.data

import kotlinx.serialization.Serializable

@Serializable
internal data class AddOrderPartRequest(
    val partId: Long,
    val quantity: Int = 1,
    val priceAtOrder: String? = null
)