package com.example.autoservice_desktop.features.services.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.autoservice_desktop.core.ui.AppDialog
import com.example.autoservice_desktop.features.services.presentation.ServicesAction
import com.example.autoservice_desktop.features.services.presentation.ServicesState

@Composable
internal fun CreateServiceDialog(
    state: ServicesState,
    onAction: (ServicesAction) -> Unit
) {
    val form = state.createForm

    AppDialog(
        title = "Добавить услугу",
        confirmText = if (form.isSubmitting) "Сохранение..." else "Сохранить",
        confirmEnabled = !form.isSubmitting,
        onConfirm = { onAction(ServicesAction.SubmitCreate) },
        onDismiss = { onAction(ServicesAction.CloseCreateDialog) }
    ) {
        OutlinedTextField(
            value = form.name,
            onValueChange = { onAction(ServicesAction.ChangeCreateName(it)) },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.description,
            onValueChange = { onAction(ServicesAction.ChangeCreateDescription(it)) },
            label = { Text("Описание") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        OutlinedTextField(
            value = form.basePrice,
            onValueChange = { onAction(ServicesAction.ChangeCreateBasePrice(it)) },
            label = { Text("Базовая цена") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.normHours,
            onValueChange = { onAction(ServicesAction.ChangeCreateNormHours(it)) },
            label = { Text("Нормо-часы") },
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