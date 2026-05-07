package com.example.autoservice_desktop.features.payments.data

import kotlinx.serialization.Serializable

@Serializable
internal data class CreatePaymentRequest(
    val amount: String,
    val paymentMethod: String
)
