package com.example.autoservice_desktop.features.orders.data

import kotlinx.serialization.Serializable

@Serializable
internal data class AddOrderPaymentRequest(
    val amount: String,
    val paymentMethod: String,
    val paymentStatus: String = "PAID"
)