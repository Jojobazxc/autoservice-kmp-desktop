package com.example.autoservice_desktop.features.orders.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.autoservice_desktop.features.orders.presentation.OrderDetailsUi
import com.example.autoservice_desktop.features.orders.presentation.OrderListItemUi
import com.example.autoservice_desktop.features.orders.presentation.OrdersAction
import com.example.autoservice_desktop.features.orders.presentation.OrdersStore

@Composable
internal fun OrdersScreen(
    store: OrdersStore
) {
    val state by store.state.collectAsState()

    LaunchedEffect(Unit) {
        store.dispatch(OrdersAction.Load)
    }

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Заказы",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        OrdersToolbar(
            onRefresh = { store.dispatch(OrdersAction.Load) })

        when {
            state.isLoadingList -> {
                Text("Загрузка заказов...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            state.listError != null -> {
                Text(
                    text = "Ошибка: ${state.listError}", color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                Row(
                    modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OrdersTable(
                        items = state.items, selectedOrderId = state.selectedOrderId, onOrderSelected = { id ->
                            store.dispatch(OrdersAction.SelectOrder(id))
                        }, modifier = Modifier.weight(1.4f)
                    )

                    OrderDetailsPanel(
                        details = state.selectedOrderDetails,
                        isLoading = state.isLoadingDetails,
                        error = state.detailsError,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrdersToolbar(
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
            label = { Text("Фильтры появятся позже") },
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onRefresh, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Обновить")
        }
    }
}

@Composable
private fun OrdersTable(
    items: List<OrderListItemUi>, selectedOrderId: Long?, onOrderSelected: (Long) -> Unit, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxHeight().border(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OrdersTableHeader()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items) { index, order ->
                    OrderTableRow(
                        index = index + 1,
                        order = order,
                        isSelected = selectedOrderId == order.id,
                        onClick = { onOrderSelected(order.id) })
                }
            }
        }
    }
}

@Composable
private fun OrdersTableHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().background(AppColors.TableHeaderBackground)
            .padding(vertical = 14.dp, horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("ID", 0.6f)
        HeaderCell("Клиент", 1.45f)
        HeaderCell("Автомобиль", 1.55f)
        HeaderCell("Мастер", 1.35f)
        HeaderCell("Статус", 1.0f)
        HeaderCell("Создан", 1.15f)
        HeaderCell("Сумма", 0.9f)
    }
}

@Composable
private fun OrderTableRow(
    index: Int, order: OrderListItemUi, isSelected: Boolean, onClick: () -> Unit
) {
    val defaultBackground = when {
        isSelected -> AppColors.SelectedItemBackground
        index % 2 == 0 -> AppColors.TableRowEven
        else -> AppColors.TableRowOdd
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val background = when {
        isSelected -> AppColors.SelectedItemBackground
        isHovered -> MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
        else -> defaultBackground
    }

    Row(
        modifier = Modifier.fillMaxWidth().background(background).clickable(
                interactionSource = interactionSource, indication = null, onClick = onClick
            ).padding(vertical = 12.dp, horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Cell(order.id.toString(), 0.6f)
        Cell(order.clientDisplay, 1.45f)
        Cell(order.carDisplay, 1.55f)
        Cell(order.masterDisplay, 1.35f)
        StatusCell(order.status, 1.0f)
        Cell(order.createdAt, 1.15f)
        PriceCell(order.totalAmount, 0.9f)
    }
}

@Composable
private fun OrderDetailsPanel(
    details: OrderDetailsUi?, isLoading: Boolean, error: String?, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxHeight().border(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text("Загрузка деталей...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ошибка: $error", color = MaterialTheme.colorScheme.error
                    )
                }
            }

            details == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Выберите заказ", color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Заказ #${details.id}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = details.clientDisplay,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            StatusBadge(details.status)
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard(
                                title = "Сумма", value = "${details.totalAmount} ₽", modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                title = "Услуги",
                                value = details.services.size.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                title = "Запчасти",
                                value = details.parts.size.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                title = "Оплаты",
                                value = details.payments.size.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        DetailCard(title = "Основная информация") {
                            TwoColumnDetails(
                                left = listOf(
                                    "Клиент" to details.clientDisplay,
                                    "Автомобиль" to details.carDisplay,
                                    "Мастер" to details.masterDisplay,
                                    "Статус" to details.status
                                ), right = listOf(
                                    "Создан" to details.createdAt,
                                    "План завершения" to details.plannedCompletionAt,
                                    "Завершен" to details.completedAt,
                                    "Сумма" to "${details.totalAmount} ₽"
                                )
                            )
                        }
                    }

                    item {
                        DetailCard(title = "Описание") {
                            DetailTextBlock(details.description)
                        }
                    }

                    item {
                        DetailCard(title = "Комментарий") {
                            DetailTextBlock(details.comment)
                        }
                    }

                    item {
                        DetailCard(title = "Услуги (${details.services.size})") {
                            if (details.services.isEmpty()) {
                                EmptySection()
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    details.services.forEach { item ->
                                        LineItemRow(
                                            title = item.serviceDisplay,
                                            value = "${item.quantity} × ${item.priceAtOrder} ₽"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        DetailCard(title = "Запчасти (${details.parts.size})") {
                            if (details.parts.isEmpty()) {
                                EmptySection()
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    details.parts.forEach { item ->
                                        LineItemRow(
                                            title = item.partDisplay,
                                            value = "${item.quantity} × ${item.priceAtOrder} ₽"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        DetailCard(title = "Оплаты (${details.payments.size})") {
                            if (details.payments.isEmpty()) {
                                EmptySection()
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    details.payments.forEach { payment ->
                                        LineItemRow(
                                            title = "Оплата #${payment.id}",
                                            value = "${payment.amount} ₽ • ${payment.paymentMethod} • ${payment.paymentStatus} • ${payment.paidAt}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String, value: String, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
private fun DetailCard(
    title: String, content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            content()
        }
    }
}

@Composable
private fun TwoColumnDetails(
    left: List<Pair<String, String>>, right: List<Pair<String, String>>
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            left.forEach { (label, value) ->
                DetailRow(label, value)
            }
        }

        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            right.forEach { (label, value) ->
                DetailRow(label, value)
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String, value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.9f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.1f)
        )
    }
}

@Composable
private fun DetailTextBlock(
    text: String
) {
    Text(
        text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun EmptySection() {
    Text(
        text = "-", color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun LineItemRow(
    title: String, value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.25f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.95f)
        )
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
private fun StatusBadge(status: String) {
    val background = MaterialTheme.colorScheme.surfaceVariant

    val textColor = when (status) {
        "Оплачен" -> AppColors.Success
        "Завершён" -> AppColors.Primary
        "В работе" -> AppColors.Warning
        "Отменён" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.background(background, MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(8.dp).background(textColor, MaterialTheme.shapes.small)
        )
        Text(
            text = status, color = textColor, style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun RowScope.StatusCell(
    status: String, weight: Float
) {
    Row(
        modifier = Modifier.weight(weight).padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        val background = MaterialTheme.colorScheme.surfaceVariant

        val textColor = when (status) {
            "Оплачен" -> AppColors.Success
            "Завершён" -> AppColors.Primary
            "В работе" -> AppColors.Warning
            "Отменён" -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }

        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.background(background, MaterialTheme.shapes.small)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}