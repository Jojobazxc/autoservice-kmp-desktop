package com.example.autoservice_desktop.features.payments.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.autoservice_desktop.core.ui.AppDialog
import com.example.autoservice_desktop.core.ui.AppDropdownField
import com.example.autoservice_desktop.core.ui.formatPaymentMethod
import com.example.autoservice_desktop.features.payments.presentation.PaymentsAction
import com.example.autoservice_desktop.features.payments.presentation.PaymentsState

@Composable
internal fun CreatePaymentDialog(
    state: PaymentsState,
    onAction: (PaymentsAction) -> Unit
) {
    val form = state.createForm

    AppDialog(
        title = "Провести оплату",
        confirmText = if (form.isSubmitting) "Сохранение..." else "Сохранить",
        confirmEnabled = !form.isSubmitting,
        onConfirm = { onAction(PaymentsAction.SubmitCreate) },
        onDismiss = { onAction(PaymentsAction.CloseCreateDialog) }
    ) {
        OutlinedTextField(
            value = form.orderId,
            onValueChange = { onAction(PaymentsAction.ChangeCreateOrderId(it)) },
            label = { Text("Номер заказа") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.amount,
            onValueChange = { onAction(PaymentsAction.ChangeCreateAmount(it)) },
            label = { Text("Сумма") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        AppDropdownField(
            label = "Способ оплаты",
            selectedValue = form.paymentMethod,
            options = listOf("CASH", "CARD"),
            optionLabel = ::formatPaymentMethod,
            onValueSelected = { onAction(PaymentsAction.ChangeCreateMethod(it)) },
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
