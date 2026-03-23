package com.example.autoservice_desktop.features.orders.data

internal class OrdersRepository(
    private val api: OrdersApi
) {
    suspend fun getOrders(): List<OrderDto> = api.getOrders()

    suspend fun getOrderDetails(orderId: Long): OrderDetailsDto = api.getOrderDetails(orderId)
}