package com.example.autoservice_desktop.features.orders.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.autoservice_desktop.core.ui.AppDialog
import com.example.autoservice_desktop.core.ui.AppDropdownField
import com.example.autoservice_desktop.core.ui.formatPaymentMethod
import com.example.autoservice_desktop.core.ui.formatPaymentStatus
import com.example.autoservice_desktop.features.orders.presentation.OrdersAction
import com.example.autoservice_desktop.features.orders.presentation.OrdersState

@Composable
internal fun AddOrderPaymentDialog(
    state: OrdersState,
    onAction: (OrdersAction) -> Unit
) {
    val form = state.addPaymentForm

    AppDialog(
        title = "Добавить оплату",
        confirmText = if (form.isSubmitting) "Сохранение..." else "Сохранить",
        confirmEnabled = !form.isSubmitting,
        onConfirm = { onAction(OrdersAction.SubmitAddPayment) },
        onDismiss = { onAction(OrdersAction.CloseAddPaymentDialog) }
    ) {
        OutlinedTextField(
            value = form.amount,
            onValueChange = { onAction(OrdersAction.ChangeAddPaymentAmount(it)) },
            label = { Text("Сумма") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        AppDropdownField(
            label = "Способ оплаты",
            selectedValue = form.paymentMethod,
            options = listOf("CASH", "CARD"),
            optionLabel = ::formatPaymentMethod,
            onValueSelected = { onAction(OrdersAction.ChangeAddPaymentMethod(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        AppDropdownField(
            label = "Статус оплаты",
            selectedValue = form.paymentStatus,
            options = listOf("PAID", "PENDING", "FAILED"),
            optionLabel = ::formatPaymentStatus,
            onValueSelected = { onAction(OrdersAction.ChangeAddPaymentStatus(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        form.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}