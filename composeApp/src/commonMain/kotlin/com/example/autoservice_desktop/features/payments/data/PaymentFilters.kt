package com.example.autoservice_desktop.features.payments.data

internal data class PaymentFilters(
    val status: String? = null,
    val method: String? = null,
    val from: String? = null,
    val to: String? = null
)
