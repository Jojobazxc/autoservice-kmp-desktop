package com.example.autoservice_desktop.features.orders.presentation

internal sealed interface OrdersAction {
    data object Load : OrdersAction
    data class SelectOrder(val orderId: Long) : OrdersAction
}