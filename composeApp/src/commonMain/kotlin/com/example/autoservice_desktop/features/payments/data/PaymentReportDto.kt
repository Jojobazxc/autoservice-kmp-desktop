package com.example.autoservice_desktop.features.payments.data

import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentReportDto(
    val totalCount: Int,
    val totalAmount: String,
    val paidAmount: String,
    val pendingAmount: String,
    val failedAmount: String
)
