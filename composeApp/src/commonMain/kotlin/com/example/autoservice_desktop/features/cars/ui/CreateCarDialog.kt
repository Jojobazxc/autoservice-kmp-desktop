package com.example.autoservice_desktop.features.cars.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.autoservice_desktop.core.ui.AppDialog
import com.example.autoservice_desktop.core.ui.AppDropdownField
import com.example.autoservice_desktop.features.cars.presentation.CarsAction
import com.example.autoservice_desktop.features.cars.presentation.CarsState

@Composable
internal fun CreateCarDialog(
    state: CarsState,
    onAction: (CarsAction) -> Unit
) {
    val form = state.createForm

    AppDialog(
        title = "Добавить автомобиль",
        confirmText = if (form.isSubmitting) "Сохранение..." else "Сохранить",
        confirmEnabled = !form.isSubmitting,
        onConfirm = { onAction(CarsAction.SubmitCreate) },
        onDismiss = { onAction(CarsAction.CloseCreateDialog) }
    ) {
        AppDropdownField(
            label = "Клиент",
            selectedValue = form.clientId?.toString().orEmpty(),
            options = state.clientOptions.map { it.id.toString() },
            optionLabel = { rawId ->
                state.clientOptions.firstOrNull { it.id.toString() == rawId }?.title ?: rawId
            },
            onValueSelected = { rawId ->
                onAction(CarsAction.ChangeCreateClient(rawId.toLongOrNull()))
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.brand,
            onValueChange = { onAction(CarsAction.ChangeCreateBrand(it)) },
            label = { Text("Марка") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.model,
            onValueChange = { onAction(CarsAction.ChangeCreateModel(it)) },
            label = { Text("Модель") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.year,
            onValueChange = { onAction(CarsAction.ChangeCreateYear(it)) },
            label = { Text("Год") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.plateNumber,
            onValueChange = { onAction(CarsAction.ChangeCreatePlateNumber(it)) },
            label = { Text("Гос. номер") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.vin,
            onValueChange = { onAction(CarsAction.ChangeCreateVin(it)) },
            label = { Text("VIN") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = form.mileage,
            onValueChange = { onAction(CarsAction.ChangeCreateMileage(it)) },
            label = { Text("Пробег") },
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