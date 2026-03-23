package com.example.autoservice_desktop.features.orders.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.autoservice_desktop.core.ui.AppDialog
import com.example.autoservice_desktop.core.ui.AppDropdownField
import com.example.autoservice_desktop.core.ui.formatOrderStatus
import com.example.autoservice_desktop.features.orders.presentation.OrdersAction
import com.example.autoservice_desktop.features.orders.presentation.OrdersState

@Composable
internal fun CreateOrderDialog(
    state: OrdersState,
    onAction: (OrdersAction) -> Unit
) {
    val form = state.createForm

    AppDialog(
        title = "Добавить заказ",
        confirmText = if (form.isSubmitting) "Сохранение..." else "Сохранить",
        confirmEnabled = !form.isSubmitting,
        onConfirm = { onAction(OrdersAction.SubmitCreate) },
        onDismiss = { onAction(OrdersAction.CloseCreateDialog) }
    ) {
        AppDropdownField(
            label = "Клиент",
            selectedValue = form.clientId?.toString().orEmpty(),
            options = state.clientOptions.map { it.id.toString() },
            optionLabel = { rawId ->
                state.clientOptions
                    .firstOrNull { it.id.toString() == rawId }
                    ?.title
                    ?: rawId
            },
            onValueSelected = { rawId ->
                onAction(OrdersAction.ChangeCreateClient(rawId.toLongOrNull()))
            },
            modifier = Modifier.fillMaxWidth()
        )

        AppDropdownField(
            label = "Автомобиль",
            selectedValue = form.carId?.toString().orEmpty(),
            options = state.carOptionsForSelectedClient.map { it.id.toString() },
            optionLabel = { rawId ->
                state.carOptionsForSelectedClient
                    .firstOrNull { it.id.toString() == rawId }
                    ?.title
                    ?: rawId
            },
            onValueSelected = { rawId ->
                onAction(OrdersAction.ChangeCreateCar(rawId.toLongOrNull()))
            },
            modifier = Modifier.fillMaxWidth()
        )

        AppDropdownField(
            label = "Мастер",
            selectedValue = form.masterId?.toString() ?: "none",
            options = listOf("none") + state.masterOptions.map { it.id.toString() },
            optionLabel = { rawId ->
                when (rawId) {
                    "none" -> "Не назначен"
                    else -> state.masterOptions
                        .firstOrNull { it.id.toString() == rawId }
                        ?.title
                        ?: rawId
                }
            },
            onValueSelected = { rawId ->
                onAction(
                    OrdersAction.ChangeCreateMaster(
                        rawId.toLongOrNull()
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        AppDropdownField(
            label = "Статус",
            selectedValue = form.status,
            options = listOf("CREATED", "IN_PROGRESS", "COMPLETED", "PAID", "CANCELED"),
            optionLabel = ::formatOrderStatus,
            onValueSelected = { onAction(OrdersAction.ChangeCreateStatus(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.plannedCompletionAt,
            onValueChange = { onAction(OrdersAction.ChangeCreatePlannedCompletionAt(it)) },
            label = { Text("План завершения") },
            placeholder = { Text("2026-03-23T18:30:00") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.description,
            onValueChange = { onAction(OrdersAction.ChangeCreateDescription(it)) },
            label = { Text("Описание") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        OutlinedTextField(
            value = form.comment,
            onValueChange = { onAction(OrdersAction.ChangeCreateComment(it)) },
            label = { Text("Комментарий") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        form.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}