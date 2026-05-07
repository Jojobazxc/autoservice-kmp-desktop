package com.example.autoservice_desktop.features.orders.presentation

internal data class OrderListItemUi(
    val id: Long,
    val clientDisplay: String,
    val carDisplay: String,
    val masterDisplay: String,
    val status: String,
    val rawStatus: String,
    val createdAt: String,
    val totalAmount: String
)

internal data class OrderDetailsUi(
    val id: Long,
    val clientId: Long,
    val carId: Long,
    val masterId: Long?,
    val clientDisplay: String,
    val carDisplay: String,
    val masterDisplay: String,
    val status: String,
    val rawStatus: String,
    val createdAt: String,
    val rawPlannedCompletionAt: String?,
    val plannedCompletionAt: String,
    val completedAt: String,
    val totalAmount: String,
    val description: String,
    val rawDescription: String?,
    val comment: String,
    val rawComment: String?,
    val services: List<OrderServiceUi>,
    val parts: List<OrderPartUi>,
    val payments: List<OrderPaymentUi>
)

internal data class OrderServiceUi(
    val serviceDisplay: String,
    val quantity: Int,
    val priceAtOrder: String
)

internal data class OrderPartUi(
    val partDisplay: String,
    val quantity: Int,
    val priceAtOrder: String
)

internal data class OrderPaymentUi(
    val id: Long,
    val amount: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val rawPaymentStatus: String,
    val paidAt: String
)
