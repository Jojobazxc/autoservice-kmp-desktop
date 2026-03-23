package com.example.autoservice_desktop.features.services.ui

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
import com.example.autoservice_desktop.core.ui.AppTableToolbar
import com.example.autoservice_desktop.core.ui.theme.AppColors
import com.example.autoservice_desktop.features.services.data.ServiceDto
import com.example.autoservice_desktop.features.services.presentation.ServicesAction
import com.example.autoservice_desktop.features.services.presentation.ServicesStore

@Composable
internal fun ServicesScreen(
    store: ServicesStore
) {
    val state by store.state.collectAsState()

    LaunchedEffect(Unit) {
        store.dispatch(ServicesAction.Load)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Услуги",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        AppTableToolbar(
            searchPlaceholder = "Поиск появится позже",
            onAdd = { store.dispatch(ServicesAction.OpenCreateDialog) },
            onRefresh = { store.dispatch(ServicesAction.Load) }
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
                ServicesTable(items = state.items)
            }
        }
    }

    if (state.isCreateDialogOpen) {
        CreateServiceDialog(
            state = state,
            onAction = store::dispatch
        )
    }
}

@Composable
private fun ServicesTable(
    items: List<ServiceDto>
) {
    Surface(
        modifier = Modifier.fillMaxSize().border(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ServicesTableHeader()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items) { index, service ->
                    ServiceTableRow(
                        index = index + 1, service = service
                    )
                }
            }
        }
    }
}

@Composable
private fun ServicesTableHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().background(AppColors.TableHeaderBackground)
            .padding(vertical = 14.dp, horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("ID", 0.6f)
        HeaderCell("Название", 1.8f)
        HeaderCell("Описание", 3.0f)
        HeaderCell("Цена", 1.0f)
        HeaderCell("Нормо-часы", 1.1f)
    }
}

@Composable
private fun ServiceTableRow(
    index: Int, service: ServiceDto
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
        modifier = Modifier.fillMaxWidth().background(background).padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Cell(service.id.toString(), 0.6f)
        Cell(service.name, 1.8f)
        Cell(service.description ?: "-", 3.0f)
        PriceCell(service.basePrice, 1.0f)
        NormHoursCell(service.normHours, 1.1f)
    }
}

@Composable
private fun RowScope.HeaderCell(
    text: String, weight: Float
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.weight(weight).padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.Cell(
    text: String, weight: Float
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.weight(weight).padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.PriceCell(
    value: String, weight: Float
) {
    Text(
        text = "$value ₽",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.weight(weight).padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.NormHoursCell(
    value: String?, weight: Float
) {
    Text(text = value?.let { "$it ч" } ?: "-",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.weight(weight).padding(end = 8.dp))
}