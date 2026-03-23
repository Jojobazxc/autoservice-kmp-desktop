package com.example.autoservice_desktop.features.orders.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class OrdersApi(
    private val httpClient: HttpClient
) {
    suspend fun getOrders(): List<OrderDto> {
        return httpClient
            .get("${ApiConfig.BASE_URL}/orders")
            .body()
    }

    suspend fun getOrderDetails(orderId: Long): OrderDetailsDto {
        return httpClient
            .get("${ApiConfig.BASE_URL}/orders/$orderId/details")
            .body()
    }

    suspend fun createOrder(request: CreateOrderRequest): OrderDto {
        return httpClient
            .post("${ApiConfig.BASE_URL}/orders") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()
    }

    suspend fun addServiceToOrder(
        orderId: Long,
        request: AddOrderServiceRequest
    ): OrderServiceItemDto {
        return httpClient
            .post("${ApiConfig.BASE_URL}/orders/$orderId/services") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()
    }

    suspend fun addPartToOrder(
        orderId: Long,
        request: AddOrderPartRequest
    ): OrderPartItemDto {
        return httpClient
            .post("${ApiConfig.BASE_URL}/orders/$orderId/parts") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()
    }

    suspend fun addPaymentToOrder(
        orderId: Long,
        request: AddOrderPaymentRequest
    ): OrderPaymentDto {
        return httpClient
            .post("${ApiConfig.BASE_URL}/orders/$orderId/payments") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()
    }
}