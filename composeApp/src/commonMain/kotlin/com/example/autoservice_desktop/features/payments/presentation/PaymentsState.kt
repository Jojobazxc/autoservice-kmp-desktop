package com.example.autoservice_desktop.features.payments.presentation

internal data class PaymentsState(
    val items: List<PaymentUi> = emptyList(),
    val report: PaymentReportUi? = null,
    val statusFilter: String = "",
    val methodFilter: String = "",
    val fromFilter: String = "",
    val toFilter: String = "",
    val isCreateDialogOpen: Boolean = false,
    val createForm: CreatePaymentForm = CreatePaymentForm(),
    val isLoading: Boolean = false,
    val error: String? = null
)

internal data class PaymentUi(
    val id: Long,
    val orderId: Long,
    val amount: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val rawPaymentMethod: String,
    val rawPaymentStatus: String,
    val paidAt: String
)

internal data class PaymentReportUi(
    val totalCount: Int,
    val totalAmount: String,
    val paidAmount: String,
    val pendingAmount: String,
    val failedAmount: String
)

internal data class CreatePaymentForm(
    val orderId: String = "",
    val amount: String = "",
    val paymentMethod: String = "CASH",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)
