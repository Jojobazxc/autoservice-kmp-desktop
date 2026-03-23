package com.example.autoservice_desktop.features.orders.presentation

internal data class OrdersState(
    val items: List<OrderListItemUi> = emptyList(),
    val selectedOrderId: Long? = null,
    val selectedOrderDetails: OrderDetailsUi? = null,

    val isLoadingList: Boolean = false,
    val isLoadingDetails: Boolean = false,

    val listError: String? = null,
    val detailsError: String? = null
)