package com.example.autoservice_desktop.features.payments.data

import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentDto(
    val id: Long,
    val orderId: Long,
    val amount: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val paidAt: String? = null
)
