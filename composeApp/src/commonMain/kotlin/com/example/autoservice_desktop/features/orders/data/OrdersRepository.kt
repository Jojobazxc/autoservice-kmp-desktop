package com.example.autoservice_desktop.features.orders.data

internal class OrdersRepository(
    private val api: OrdersApi
) {
    suspend fun getOrders(): List<OrderDto> = api.getOrders()

    suspend fun getOrderDetails(orderId: Long): OrderDetailsDto = api.getOrderDetails(orderId)

    suspend fun createOrder(request: CreateOrderRequest): OrderDto = api.createOrder(request)

    suspend fun updateOrder(orderId: Long, request: UpdateOrderRequest): OrderDto = api.updateOrder(orderId, request)

    suspend fun completeOrder(orderId: Long): OrderDto = api.completeOrder(orderId)

    suspend fun cancelOrder(orderId: Long): OrderDto = api.cancelOrder(orderId)

    suspend fun addServiceToOrder(
        orderId: Long,
        request: AddOrderServiceRequest
    ): OrderServiceItemDto = api.addServiceToOrder(orderId, request)

    suspend fun addPartToOrder(
        orderId: Long,
        request: AddOrderPartRequest
    ): OrderPartItemDto = api.addPartToOrder(orderId, request)

    suspend fun addPaymentToOrder(
        orderId: Long,
        request: AddOrderPaymentRequest
    ): OrderPaymentDto = api.addPaymentToOrder(orderId, request)
}
