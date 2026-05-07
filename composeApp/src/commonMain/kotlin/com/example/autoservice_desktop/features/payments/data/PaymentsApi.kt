package com.example.autoservice_desktop.features.payments.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class PaymentsApi(
    private val httpClient: HttpClient
) {
    suspend fun getPayments(filters: PaymentFilters): List<PaymentDto> {
        return httpClient
            .get("${ApiConfig.BASE_URL}/payments") {
                applyFilters(filters)
            }
            .body()
    }

    suspend fun getPaymentReport(filters: PaymentFilters): PaymentReportDto {
        return httpClient
            .get("${ApiConfig.BASE_URL}/payments/report") {
                applyFilters(filters)
            }
            .body()
    }

    suspend fun createPayment(orderId: Long, request: CreatePaymentRequest): PaymentDto {
        return httpClient
            .post("${ApiConfig.BASE_URL}/orders/$orderId/payments") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()
    }

    suspend fun payPayment(paymentId: Long): PaymentDto {
        return httpClient
            .post("${ApiConfig.BASE_URL}/payments/$paymentId/pay")
            .body()
    }

    private fun HttpRequestBuilder.applyFilters(filters: PaymentFilters) {
        filters.status?.let { parameter("status", it) }
        filters.method?.let { parameter("method", it) }
        filters.from?.let { parameter("from", it) }
        filters.to?.let { parameter("to", it) }
    }
}
