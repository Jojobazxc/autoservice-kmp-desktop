package com.example.autoservice_desktop.features.payments.data

import kotlinx.serialization.Serializable

@Serializable
internal data class AccountingSummaryDto(
    val period: AccountingPeriodDto,
    val totals: AccountingTotalsDto,
    val byStatus: List<AccountingPaymentStatusDto>
)

@Serializable
internal data class AccountingPeriodDto(
    val from: String? = null,
    val to: String? = null
)

@Serializable
internal data class AccountingTotalsDto(
    val revenue: String,
    val paid: String,
    val unpaid: String,
    val ordersCount: Int
)

@Serializable
internal data class AccountingPaymentStatusDto(
    val status: String,
    val amount: String,
    val count: Int
)

@Serializable
internal data class AccountingPaymentsPageDto(
    val items: List<AccountingPaymentItemDto>,
    val page: Int,
    val limit: Int,
    val total: Int,
    val totals: AccountingPaymentsTotalsDto
)

@Serializable
internal data class AccountingPaymentsTotalsDto(
    val amount: String,
    val count: Int,
    val paidAmount: String,
    val pendingAmount: String,
    val failedAmount: String
)

@Serializable
internal data class AccountingPaymentItemDto(
    val paymentId: Long,
    val orderId: Long,
    val clientName: String,
    val car: String,
    val amount: String,
    val status: String,
    val method: String,
    val paidAt: String? = null
)

@Serializable
internal data class AccountingDebtsPageDto(
    val items: List<AccountingDebtItemDto>,
    val page: Int,
    val limit: Int,
    val total: Int,
    val totals: AccountingDebtsTotalsDto
)

@Serializable
internal data class AccountingDebtsTotalsDto(
    val totalAmount: String,
    val paidAmount: String,
    val debtAmount: String,
    val ordersCount: Int
)

@Serializable
internal data class AccountingDebtItemDto(
    val orderId: Long,
    val clientName: String,
    val car: String,
    val totalAmount: String,
    val paidAmount: String,
    val debtAmount: String,
    val orderStatus: String,
    val lastPaymentAt: String? = null
)
