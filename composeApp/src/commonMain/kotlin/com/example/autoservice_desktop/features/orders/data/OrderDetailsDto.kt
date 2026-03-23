package com.example.autoservice_desktop.features.orders.data

import kotlinx.serialization.Serializable

@Serializable
internal data class OrderDetailsDto(
    val order: OrderDto,
    val services: List<OrderServiceItemDto>,
    val parts: List<OrderPartItemDto>,
    val payments: List<OrderPaymentDto>
)

@Serializable
internal data class OrderServiceItemDto(
    val orderId: Long,
    val serviceId: Long,
    val quantity: Int,
    val priceAtOrder: String
)

@Serializable
internal data class OrderPartItemDto(
    val orderId: Long,
    val partId: Long,
    val quantity: Int,
    val priceAtOrder: String
)

@Serializable
internal data class OrderPaymentDto(
    val id: Long,
    val orderId: Long,
    val amount: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val paidAt: String? = null
)