package com.example.autoservice_desktop.features.clients.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.autoservice_desktop.core.ui.AppDialog
import com.example.autoservice_desktop.core.ui.AppDropdownField
import com.example.autoservice_desktop.core.ui.formatClientStatus
import com.example.autoservice_desktop.features.clients.presentation.ClientsAction
import com.example.autoservice_desktop.features.clients.presentation.ClientsState

@Composable
internal fun CreateClientDialog(
    state: ClientsState,
    onAction: (ClientsAction) -> Unit
) {
    AppDialog(
        title = "Добавить клиента",
        confirmText = if (state.isCreating) "Сохранение..." else "Сохранить",
        confirmEnabled = !state.isCreating,
        onConfirm = { onAction(ClientsAction.SubmitCreate) },
        onDismiss = { onAction(ClientsAction.CloseCreateDialog) }
    ) {
        OutlinedTextField(
            value = state.fullNameInput,
            onValueChange = { onAction(ClientsAction.UpdateFullName(it)) },
            label = { Text("ФИО") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.phoneInput,
            onValueChange = { onAction(ClientsAction.UpdatePhone(it)) },
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.emailInput,
            onValueChange = { onAction(ClientsAction.UpdateEmail(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.addressInput,
            onValueChange = { onAction(ClientsAction.UpdateAddress(it)) },
            label = { Text("Адрес") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        AppDropdownField(
            label = "Статус",
            selectedValue = state.statusInput,
            options = listOf("REGULAR", "VIP"),
            optionLabel = ::formatClientStatus,
            onValueSelected = { onAction(ClientsAction.UpdateStatus(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        state.createError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}