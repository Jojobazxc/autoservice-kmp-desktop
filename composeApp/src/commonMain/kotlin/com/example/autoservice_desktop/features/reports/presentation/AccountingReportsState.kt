package com.example.autoservice_desktop.features.reports.presentation

internal data class AccountingReportsState(
    val reportType: AccountingReportType = AccountingReportType.Payments,
    val fromFilter: String = "",
    val toFilter: String = "",
    val statusFilter: String = "",
    val methodFilter: String = "",
    val clientIdFilter: String = "",
    val orderIdFilter: String = "",
    val orderStatusFilter: String = "",
    val clientOptions: List<AccountingClientOptionUi> = emptyList(),
    val summary: AccountingSummaryUi? = null,
    val payments: AccountingPaymentsPageUi = AccountingPaymentsPageUi(),
    val debts: AccountingDebtsPageUi = AccountingDebtsPageUi(),
    val isLoading: Boolean = false,
    val hasGeneratedReport: Boolean = false,
    val error: String? = null
)

internal data class AccountingClientOptionUi(
    val id: Long,
    val title: String
)

internal enum class AccountingReportType {
    Summary,
    Payments,
    Debts
}

internal data class AccountingSummaryUi(
    val revenue: String,
    val paid: String,
    val unpaid: String,
    val ordersCount: Int,
    val byStatus: List<AccountingStatusUi>
)

internal data class AccountingStatusUi(
    val status: String,
    val amount: String,
    val count: Int
)

internal data class AccountingPaymentsPageUi(
    val items: List<AccountingPaymentUi> = emptyList(),
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0,
    val totals: AccountingPaymentsTotalsUi? = null
)

internal data class AccountingPaymentsTotalsUi(
    val amount: String,
    val count: Int,
    val paidAmount: String,
    val pendingAmount: String,
    val failedAmount: String
)

internal data class AccountingPaymentUi(
    val paymentId: Long,
    val orderId: Long,
    val clientName: String,
    val car: String,
    val amount: String,
    val status: String,
    val method: String,
    val paidAt: String
)

internal data class AccountingDebtsPageUi(
    val items: List<AccountingDebtUi> = emptyList(),
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0,
    val totals: AccountingDebtsTotalsUi? = null
)

internal data class AccountingDebtsTotalsUi(
    val totalAmount: String,
    val paidAmount: String,
    val debtAmount: String,
    val ordersCount: Int
)

internal data class AccountingDebtUi(
    val orderId: Long,
    val clientName: String,
    val car: String,
    val totalAmount: String,
    val paidAmount: String,
    val debtAmount: String,
    val orderStatus: String,
    val lastPaymentAt: String
)
