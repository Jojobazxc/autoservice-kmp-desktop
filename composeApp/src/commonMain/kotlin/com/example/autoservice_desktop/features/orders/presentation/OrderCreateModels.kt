package com.example.autoservice_desktop.features.orders.presentation

internal data class CreateOrderForm(
    val clientId: Long? = null,
    val carId: Long? = null,
    val masterId: Long? = null,
    val description: String = "",
    val comment: String = "",
    val status: String = "CREATED",
    val plannedCompletionAt: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

internal data class CreateOrderItemForm(
    val selectedId: Long? = null,
    val quantity: String = "1",
    val priceAtOrder: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

internal data class CreateOrderPaymentForm(
    val amount: String = "",
    val paymentMethod: String = "CASH",
    val paymentStatus: String = "PAID",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

internal data class ReferenceOptionUi(
    val id: Long,
    val title: String
)