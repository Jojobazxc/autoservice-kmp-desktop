package com.example.autoservice_desktop.features.payments.presentation

internal sealed interface PaymentsAction {
    data object Load : PaymentsAction
    data class ChangeStatus(val value: String) : PaymentsAction
    data class ChangeMethod(val value: String) : PaymentsAction
    data class ChangeFrom(val value: String) : PaymentsAction
    data class ChangeTo(val value: String) : PaymentsAction
    data object ClearFilters : PaymentsAction
    data object OpenCreateDialog : PaymentsAction
    data object CloseCreateDialog : PaymentsAction
    data class ChangeCreateOrderId(val value: String) : PaymentsAction
    data class ChangeCreateAmount(val value: String) : PaymentsAction
    data class ChangeCreateMethod(val value: String) : PaymentsAction
    data object SubmitCreate : PaymentsAction
    data class PayPayment(val paymentId: Long) : PaymentsAction
}
