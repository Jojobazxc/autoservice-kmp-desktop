package com.example.autoservice_desktop.features.orders.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.autoservice_desktop.core.ui.AppDialog
import com.example.autoservice_desktop.core.ui.AppDropdownField
import com.example.autoservice_desktop.features.orders.presentation.OrdersAction
import com.example.autoservice_desktop.features.orders.presentation.OrdersState

@Composable
internal fun AddOrderServiceDialog(
    state: OrdersState,
    onAction: (OrdersAction) -> Unit
) {
    val form = state.addServiceForm

    AppDialog(
        title = "Добавить услугу",
        confirmText = if (form.isSubmitting) "Сохранение..." else "Сохранить",
        confirmEnabled = !form.isSubmitting,
        onConfirm = { onAction(OrdersAction.SubmitAddService) },
        onDismiss = { onAction(OrdersAction.CloseAddServiceDialog) }
    ) {
        AppDropdownField(
            label = "Услуга",
            selectedValue = form.selectedId?.toString().orEmpty(),
            options = state.serviceOptions.map { it.id.toString() },
            optionLabel = { rawId ->
                state.serviceOptions
                    .firstOrNull { it.id.toString() == rawId }
                    ?.title
                    ?: rawId
            },
            onValueSelected = { rawId ->
                onAction(OrdersAction.ChangeAddServiceSelected(rawId.toLongOrNull()))
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.quantity,
            onValueChange = { onAction(OrdersAction.ChangeAddServiceQuantity(it)) },
            label = { Text("Количество") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.priceAtOrder,
            onValueChange = { onAction(OrdersAction.ChangeAddServicePrice(it)) },
            label = { Text("Цена на момент заказа") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        form.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}