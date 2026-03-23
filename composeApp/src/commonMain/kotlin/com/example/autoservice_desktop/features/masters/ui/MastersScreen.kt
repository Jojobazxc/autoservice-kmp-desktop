package com.example.autoservice_desktop.features.masters.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autoservice_desktop.core.ui.formatEmploymentStatus
import com.example.autoservice_desktop.core.ui.theme.AppColors
import com.example.autoservice_desktop.features.masters.data.MasterDto
import com.example.autoservice_desktop.features.masters.presentation.MastersAction
import com.example.autoservice_desktop.features.masters.presentation.MastersStore

@Composable
internal fun MastersScreen(
    store: MastersStore
) {
    val state by store.state.collectAsState()

    LaunchedEffect(Unit) {
        store.dispatch(MastersAction.Load)
    }

    if (state.isCreateDialogOpen) {
        CreateMasterDialog(
            state = state,
            onAction = store::dispatch
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Мастера",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        MastersToolbar(
            onRefresh = { store.dispatch(MastersAction.Load) },
            onAdd = { store.dispatch(MastersAction.OpenCreateDialog) }
        )

        when {
            state.isLoading -> {
                Text("Загрузка...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            state.error != null -> {
                Text(
                    text = "Ошибка: ${state.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                MastersTable(items = state.items)
            }
        }
    }
}

@Composable
private fun MastersToolbar(
    onRefresh: () -> Unit,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            enabled = false,
            label = { Text("Поиск появится позже") },
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onRefresh,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Обновить")
        }

        Button(
            onClick = onAdd,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Добавить")
        }
    }
}

@Composable
private fun MastersTable(
    items: List<MasterDto>
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MastersTableHeader()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items) { index, master ->
                    MasterTableRow(
                        index = index + 1,
                        master = master
                    )
                }
            }
        }
    }
}

@Composable
private fun MastersTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.TableHeaderBackground)
            .padding(vertical = 14.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("ID", 0.6f)
        HeaderCell("ФИО", 1.8f)
        HeaderCell("Специализация", 1.8f)
        HeaderCell("Стаж", 0.8f)
        HeaderCell("Телефон", 1.4f)
        HeaderCell("Email", 1.8f)
        HeaderCell("Статус", 1.0f)
    }
}

@Composable
private fun MasterTableRow(
    index: Int,
    master: MasterDto
) {
    val defaultBackground = if (index % 2 == 0) {
        AppColors.TableRowEven
    } else {
        AppColors.TableRowOdd
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val background = if (isHovered) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
    } else {
        defaultBackground
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Cell(master.id.toString(), 0.6f)
        Cell(master.fullName, 1.8f)
        Cell(master.specialization ?: "-", 1.8f)
        Cell(master.experienceYears?.toString() ?: "-", 0.8f)
        Cell(master.phone ?: "-", 1.4f)
        Cell(master.email ?: "-", 1.8f)
        EmploymentStatusCell(formatEmploymentStatus(master.employmentStatus), 1.0f)
    }
}

@Composable
private fun RowScope.HeaderCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .weight(weight)
            .padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.Cell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .weight(weight)
            .padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.EmploymentStatusCell(
    status: String,
    weight: Float
) {
    Row(
        modifier = Modifier
            .weight(weight)
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val background = MaterialTheme.colorScheme.surfaceVariant

        val textColor = if (status == "Активен") {
            AppColors.Success
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .background(background, MaterialTheme.shapes.small)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}