package com.example.autoservice_desktop.features.payments.data

internal class PaymentsRepository(
    private val api: PaymentsApi
) {
    suspend fun getPayments(filters: PaymentFilters): List<PaymentDto> = api.getPayments(filters)

    suspend fun getPaymentReport(filters: PaymentFilters): PaymentReportDto = api.getPaymentReport(filters)

    suspend fun createPayment(orderId: Long, request: CreatePaymentRequest): PaymentDto {
        return api.createPayment(orderId, request)
    }

    suspend fun payPayment(paymentId: Long): PaymentDto = api.payPayment(paymentId)
}
