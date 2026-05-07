package com.example.autoservice_desktop.features.payments.data

internal data class AccountingReportFilters(
    val from: String? = null,
    val to: String? = null,
    val status: String? = null,
    val method: String? = null,
    val clientId: Long? = null,
    val orderId: Long? = null,
    val orderStatus: String? = null,
    val paymentsPage: Int = 1,
    val debtsPage: Int = 1,
    val limit: Int = 20,
    val paymentsSort: String = "paidAt,desc",
    val debtsSort: String = "debtAmount,desc"
)
