package com.example.autoservice_desktop.features.orders.presentation

internal data class OrdersState(
    val items: List<OrderListItemUi> = emptyList(),
    val selectedOrderId: Long? = null,
    val selectedOrderDetails: OrderDetailsUi? = null,
    val isLoadingList: Boolean = false,
    val isLoadingDetails: Boolean = false,
    val listError: String? = null,
    val detailsError: String? = null,

    val isCreateDialogOpen: Boolean = false,
    val createForm: CreateOrderForm = CreateOrderForm(),

    val clientOptions: List<ReferenceOptionUi> = emptyList(),
    val masterOptions: List<ReferenceOptionUi> = emptyList(),
    val carOptionsForSelectedClient: List<ReferenceOptionUi> = emptyList(),

    val isAddServiceDialogOpen: Boolean = false,
    val addServiceForm: CreateOrderItemForm = CreateOrderItemForm(),
    val serviceOptions: List<ReferenceOptionUi> = emptyList(),

    val isAddPartDialogOpen: Boolean = false,
    val addPartForm: CreateOrderItemForm = CreateOrderItemForm(),
    val partOptions: List<ReferenceOptionUi> = emptyList(),

    val isAddPaymentDialogOpen: Boolean = false,
    val addPaymentForm: CreateOrderPaymentForm = CreateOrderPaymentForm()
)