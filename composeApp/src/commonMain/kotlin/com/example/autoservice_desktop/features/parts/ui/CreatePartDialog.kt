package com.example.autoservice_desktop.features.parts.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.autoservice_desktop.core.ui.AppDialog
import com.example.autoservice_desktop.features.parts.presentation.PartsAction
import com.example.autoservice_desktop.features.parts.presentation.PartsState

@Composable
internal fun CreatePartDialog(
    state: PartsState,
    onAction: (PartsAction) -> Unit
) {
    val form = state.createForm

    AppDialog(
        title = "Добавить запчасть",
        confirmText = if (form.isSubmitting) "Сохранение..." else "Сохранить",
        confirmEnabled = !form.isSubmitting,
        onConfirm = { onAction(PartsAction.SubmitCreate) },
        onDismiss = { onAction(PartsAction.CloseCreateDialog) }
    ) {
        OutlinedTextField(
            value = form.name,
            onValueChange = { onAction(PartsAction.ChangeCreateName(it)) },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.article,
            onValueChange = { onAction(PartsAction.ChangeCreateArticle(it)) },
            label = { Text("Артикул") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.price,
            onValueChange = { onAction(PartsAction.ChangeCreatePrice(it)) },
            label = { Text("Цена") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.unit,
            onValueChange = { onAction(PartsAction.ChangeCreateUnit(it)) },
            label = { Text("Ед. измерения") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.stockQuantity,
            onValueChange = { onAction(PartsAction.ChangeCreateStockQuantity(it)) },
            label = { Text("Остаток") },
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