package com.example.autoservice_desktop.features.cars.ui

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
import com.example.autoservice_desktop.core.ui.theme.AppColors
import com.example.autoservice_desktop.features.cars.data.CarDto
import com.example.autoservice_desktop.features.cars.presentation.CarListItem
import com.example.autoservice_desktop.features.cars.presentation.CarsAction
import com.example.autoservice_desktop.features.cars.presentation.CarsStore

@Composable
internal fun CarsScreen(
    store: CarsStore
) {
    val state by store.state.collectAsState()

    LaunchedEffect(Unit) {
        store.dispatch(CarsAction.Load)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Автомобили",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        CarsToolbar(
            onRefresh = { store.dispatch(CarsAction.Load) }
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
                CarsTable(items = state.items)
            }
        }
    }
}

@Composable
private fun CarsToolbar(
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
private fun CarsTable(
    items: List<CarListItem>
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
            CarsTableHeader()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items) { index, car ->
                    CarTableRow(
                        index = index + 1,
                        car = car
                    )
                }
            }
        }
    }
}

@Composable
private fun CarsTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.TableHeaderBackground)
            .padding(vertical = 14.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("ID", 0.6f)
        HeaderCell("Владелец", 1.9f)
        HeaderCell("Марка", 1.2f)
        HeaderCell("Модель", 1.4f)
        HeaderCell("Год", 0.8f)
        HeaderCell("Гос. номер", 1.3f)
        HeaderCell("VIN", 2.0f)
        HeaderCell("Пробег", 1.0f)
    }
}

@Composable
private fun CarTableRow(
    index: Int,
    car: CarListItem
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
        Cell(car.id.toString(), 0.6f)
        Cell(car.ownerName, 1.9f)
        Cell(car.brand, 1.2f)
        Cell(car.model, 1.4f)
        Cell(car.year?.toString() ?: "-", 0.8f)
        Cell(car.plateNumber, 1.3f)
        Cell(car.vin ?: "-", 2.0f)
        Cell(car.mileage?.toString() ?: "-", 1.0f)
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