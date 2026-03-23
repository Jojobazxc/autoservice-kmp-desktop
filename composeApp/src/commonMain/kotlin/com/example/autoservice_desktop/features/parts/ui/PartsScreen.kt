package com.example.autoservice_desktop.features.parts.ui

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
import com.example.autoservice_desktop.core.ui.formatPartUnit
import com.example.autoservice_desktop.core.ui.theme.AppColors
import com.example.autoservice_desktop.features.parts.data.PartDto
import com.example.autoservice_desktop.features.parts.presentation.PartsAction
import com.example.autoservice_desktop.features.parts.presentation.PartsStore

@Composable
internal fun PartsScreen(
    store: PartsStore
) {
    val state by store.state.collectAsState()

    LaunchedEffect(Unit) {
        store.dispatch(PartsAction.Load)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Запчасти",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        PartsToolbar(
            onRefresh = { store.dispatch(PartsAction.Load) }
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
                PartsTable(items = state.items)
            }
        }
    }
}

@Composable
private fun PartsToolbar(
    onRefresh: () -> Unit
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
            onClick = {},
            enabled = false,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text("Добавить")
        }
    }
}

@Composable
private fun PartsTable(
    items: List<PartDto>
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
            PartsTableHeader()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items) { index, part ->
                    PartTableRow(
                        index = index + 1,
                        part = part
                    )
                }
            }
        }
    }
}

@Composable
private fun PartsTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.TableHeaderBackground)
            .padding(vertical = 14.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("ID", 0.6f)
        HeaderCell("Название", 2.2f)
        HeaderCell("Артикул", 1.5f)
        HeaderCell("Цена", 1.0f)
        HeaderCell("Ед.", 0.8f)
        HeaderCell("Остаток", 1.0f)
    }
}

@Composable
private fun PartTableRow(
    index: Int,
    part: PartDto
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
        Cell(part.id.toString(), 0.6f)
        Cell(part.name, 2.2f)
        Cell(part.article, 1.5f)
        PriceCell(part.price, 1.0f)
        Cell(formatPartUnit(part.unit), 0.8f)
        StockCell(part.stockQuantity, 1.0f)
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
private fun RowScope.PriceCell(
    value: String,
    weight: Float
) {
    Text(
        text = "$value ₽",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .weight(weight)
            .padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.StockCell(
    stockQuantity: Int,
    weight: Float
) {
    val color = when {
        stockQuantity <= 3 -> MaterialTheme.colorScheme.error
        stockQuantity <= 10 -> AppColors.Warning
        else -> AppColors.Success
    }

    Text(
        text = stockQuantity.toString(),
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = Modifier
            .weight(weight)
            .padding(end = 8.dp)
    )
}