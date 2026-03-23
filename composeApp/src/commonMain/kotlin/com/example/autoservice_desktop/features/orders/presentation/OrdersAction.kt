package com.example.autoservice_desktop.features.orders.presentation

internal sealed interface OrdersAction {
    data object Load : OrdersAction
    data class SelectOrder(val orderId: Long) : OrdersAction

    data object OpenCreateDialog : OrdersAction
    data object CloseCreateDialog : OrdersAction
    data class ChangeCreateClient(val clientId: Long?) : OrdersAction
    data class ChangeCreateCar(val carId: Long?) : OrdersAction
    data class ChangeCreateMaster(val masterId: Long?) : OrdersAction
    data class ChangeCreateDescription(val value: String) : OrdersAction
    data class ChangeCreateComment(val value: String) : OrdersAction
    data class ChangeCreateStatus(val value: String) : OrdersAction
    data class ChangeCreatePlannedCompletionAt(val value: String) : OrdersAction
    data object SubmitCreate : OrdersAction

    data object OpenAddServiceDialog : OrdersAction
    data object CloseAddServiceDialog : OrdersAction
    data class ChangeAddServiceSelected(val serviceId: Long?) : OrdersAction
    data class ChangeAddServiceQuantity(val value: String) : OrdersAction
    data class ChangeAddServicePrice(val value: String) : OrdersAction
    data object SubmitAddService : OrdersAction

    data object OpenAddPartDialog : OrdersAction
    data object CloseAddPartDialog : OrdersAction
    data class ChangeAddPartSelected(val partId: Long?) : OrdersAction
    data class ChangeAddPartQuantity(val value: String) : OrdersAction
    data class ChangeAddPartPrice(val value: String) : OrdersAction
    data object SubmitAddPart : OrdersAction

    data object OpenAddPaymentDialog : OrdersAction
    data object CloseAddPaymentDialog : OrdersAction
    data class ChangeAddPaymentAmount(val value: String) : OrdersAction
    data class ChangeAddPaymentMethod(val value: String) : OrdersAction
    data class ChangeAddPaymentStatus(val value: String) : OrdersAction
    data object SubmitAddPayment : OrdersAction
}