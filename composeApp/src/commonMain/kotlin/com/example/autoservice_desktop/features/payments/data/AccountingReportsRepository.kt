package com.example.autoservice_desktop.features.payments.data

internal class AccountingReportsRepository(
    private val api: AccountingReportsApi
) {
    suspend fun getSummary(filters: AccountingReportFilters): AccountingSummaryDto = api.getSummary(filters)

    suspend fun getPayments(filters: AccountingReportFilters): AccountingPaymentsPageDto = api.getPayments(filters)

    suspend fun getDebts(filters: AccountingReportFilters): AccountingDebtsPageDto = api.getDebts(filters)
}
