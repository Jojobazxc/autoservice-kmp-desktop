package com.example.autoservice_desktop.features.masters.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.autoservice_desktop.core.ui.AppDialog
import com.example.autoservice_desktop.core.ui.AppDropdownField
import com.example.autoservice_desktop.core.ui.formatEmploymentStatus
import com.example.autoservice_desktop.features.masters.presentation.MastersAction
import com.example.autoservice_desktop.features.masters.presentation.MastersState

@Composable
internal fun CreateMasterDialog(
    state: MastersState,
    onAction: (MastersAction) -> Unit
) {
    AppDialog(
        title = "Добавить мастера",
        confirmText = if (state.isCreating) "Сохранение..." else "Сохранить",
        confirmEnabled = !state.isCreating,
        onConfirm = { onAction(MastersAction.SubmitCreate) },
        onDismiss = { onAction(MastersAction.CloseCreateDialog) }
    ) {
        OutlinedTextField(
            value = state.fullNameInput,
            onValueChange = { onAction(MastersAction.UpdateFullName(it)) },
            label = { Text("ФИО") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.specializationInput,
            onValueChange = { onAction(MastersAction.UpdateSpecialization(it)) },
            label = { Text("Специализация") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.experienceYearsInput,
            onValueChange = { onAction(MastersAction.UpdateExperienceYears(it)) },
            label = { Text("Стаж (лет)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.phoneInput,
            onValueChange = { onAction(MastersAction.UpdatePhone(it)) },
            label = { Text("Телефон") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.emailInput,
            onValueChange = { onAction(MastersAction.UpdateEmail(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        AppDropdownField(
            label = "Статус занятости",
            selectedValue = state.employmentStatusInput,
            options = listOf("ACTIVE", "INACTIVE"),
            optionLabel = ::formatEmploymentStatus,
            onValueSelected = { onAction(MastersAction.UpdateEmploymentStatus(it)) },
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