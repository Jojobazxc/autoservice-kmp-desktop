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
internal fun AddOrderPartDialog(
    state: OrdersState,
    onAction: (OrdersAction) -> Unit
) {
    val form = state.addPartForm

    AppDialog(
        title = "Добавить запчасть",
        confirmText = if (form.isSubmitting) "Сохранение..." else "Сохранить",
        confirmEnabled = !form.isSubmitting,
        onConfirm = { onAction(OrdersAction.SubmitAddPart) },
        onDismiss = { onAction(OrdersAction.CloseAddPartDialog) }
    ) {
        AppDropdownField(
            label = "Запчасть",
            selectedValue = form.selectedId?.toString().orEmpty(),
            options = state.partOptions.map { it.id.toString() },
            optionLabel = { rawId ->
                state.partOptions
                    .firstOrNull { it.id.toString() == rawId }
                    ?.title
                    ?: rawId
            },
            onValueSelected = { rawId ->
                onAction(OrdersAction.ChangeAddPartSelected(rawId.toLongOrNull()))
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.quantity,
            onValueChange = { onAction(OrdersAction.ChangeAddPartQuantity(it)) },
            label = { Text("Количество") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.priceAtOrder,
            onValueChange = { onAction(OrdersAction.ChangeAddPartPrice(it)) },
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