package com.example.autoservice_desktop.features.payments.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class AccountingReportsApi(
    private val httpClient: HttpClient
) {
    suspend fun getSummary(filters: AccountingReportFilters): AccountingSummaryDto {
        return httpClient
            .get("${ApiConfig.BASE_URL}/api/reports/accounting/summary") {
                applyPeriod(filters)
            }
            .body()
    }

    suspend fun getPayments(filters: AccountingReportFilters): AccountingPaymentsPageDto {
        return httpClient
            .get("${ApiConfig.BASE_URL}/api/reports/accounting/payments") {
                applyPeriod(filters)
                filters.status?.let { parameter("status", it) }
                filters.method?.let { parameter("method", it) }
                filters.clientId?.let { parameter("clientId", it) }
                filters.orderId?.let { parameter("orderId", it) }
                parameter("page", filters.paymentsPage)
                parameter("limit", filters.limit)
                parameter("sort", filters.paymentsSort)
            }
            .body()
    }

    suspend fun getDebts(filters: AccountingReportFilters): AccountingDebtsPageDto {
        return httpClient
            .get("${ApiConfig.BASE_URL}/api/reports/accounting/debts") {
                applyPeriod(filters)
                filters.clientId?.let { parameter("clientId", it) }
                filters.orderStatus?.let { parameter("orderStatus", it) }
                parameter("page", filters.debtsPage)
                parameter("limit", filters.limit)
                parameter("sort", filters.debtsSort)
            }
            .body()
    }

    private fun HttpRequestBuilder.applyPeriod(filters: AccountingReportFilters) {
        filters.from?.let { parameter("from", it) }
        filters.to?.let { parameter("to", it) }
    }
}
