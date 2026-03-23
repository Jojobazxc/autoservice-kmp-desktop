package com.example.autoservice_desktop.features.orders.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class OrdersApi(
    private val httpClient: HttpClient
) {
    suspend fun getOrders(): List<OrderDto> {
        return httpClient
            .get("${ApiConfig.BASE_URL}/orders")
            .body<List<OrderDto>>()
    }

    suspend fun getOrderDetails(orderId: Long): OrderDetailsDto {
        return httpClient
            .get("${ApiConfig.BASE_URL}/orders/$orderId/details")
            .body<OrderDetailsDto>()
    }
}