package com.example.autoservice_desktop.features.payments.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autoservice_desktop.core.ui.AppDatePickerField
import com.example.autoservice_desktop.core.ui.AppDropdownField
import com.example.autoservice_desktop.core.ui.AppTableToolbar
import com.example.autoservice_desktop.core.ui.formatPaymentMethod
import com.example.autoservice_desktop.core.ui.formatPaymentStatus
import com.example.autoservice_desktop.core.ui.theme.AppColors
import com.example.autoservice_desktop.features.payments.presentation.PaymentReportUi
import com.example.autoservice_desktop.features.payments.presentation.PaymentUi
import com.example.autoservice_desktop.features.payments.presentation.PaymentsAction
import com.example.autoservice_desktop.features.payments.presentation.PaymentsState
import com.example.autoservice_desktop.features.payments.presentation.PaymentsStore

@Composable
internal fun PaymentsScreen(
    store: PaymentsStore
) {
    val state by store.state.collectAsState()

    LaunchedEffect(Unit) {
        store.dispatch(PaymentsAction.Load)
    }

    if (state.isCreateDialogOpen) {
        CreatePaymentDialog(
            state = state,
            onAction = store::dispatch
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Оплаты",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        PaymentFiltersPanel(
            state = state,
            onAction = store::dispatch
        )

        state.report?.let { report ->
            PaymentsReport(report)
        }

        AppTableToolbar(
            searchPlaceholder = "Поиск появится позже",
            addText = "Создать оплату",
            onAdd = { store.dispatch(PaymentsAction.OpenCreateDialog) },
            onRefresh = { store.dispatch(PaymentsAction.Load) }
        )

        when {
            state.isLoading -> Text("Загрузка оплат...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            state.error != null -> Text("Ошибка: ${state.error}", color = MaterialTheme.colorScheme.error)
            else -> PaymentsTable(
                items = state.items,
                onPay = { payment -> store.dispatch(PaymentsAction.PayPayment(payment.id)) }
            )
        }
    }
}

@Composable
private fun PaymentFiltersPanel(
    state: PaymentsState,
    onAction: (PaymentsAction) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppDropdownField(
                    label = "Статус",
                    selectedValue = state.statusFilter,
                    options = listOf("", "PENDING", "PAID", "FAILED"),
                    optionLabel = { value -> value.ifBlank { "Все" }.let(::formatPaymentStatusOrAll) },
                    onValueSelected = { onAction(PaymentsAction.ChangeStatus(it)) },
                    modifier = Modifier.weight(1f)
                )

                AppDropdownField(
                    label = "Метод",
                    selectedValue = state.methodFilter,
                    options = listOf("", "CASH", "CARD"),
                    optionLabel = { value -> value.ifBlank { "Все" }.let(::formatPaymentMethodOrAll) },
                    onValueSelected = { onAction(PaymentsAction.ChangeMethod(it)) },
                    modifier = Modifier.weight(1f)
                )

                AppDatePickerField(
                    label = "С даты",
                    isoDate = state.fromFilter,
                    onDateSelected = { onAction(PaymentsAction.ChangeFrom(it)) },
                    modifier = Modifier.weight(1f)
                )

                AppDatePickerField(
                    label = "По дату",
                    isoDate = state.toFilter,
                    onDateSelected = { onAction(PaymentsAction.ChangeTo(it)) },
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { onAction(PaymentsAction.Load) },
                    modifier = Modifier.widthIn(min = 116.dp)
                ) {
                    Text("Применить")
                }

                OutlinedButton(
                    onClick = { onAction(PaymentsAction.ClearFilters) },
                    modifier = Modifier.widthIn(min = 116.dp)
                ) {
                    Text("Сбросить")
                }
            }
        }
    }
}

@Composable
private fun PaymentsReport(report: PaymentReportUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ReportCard("Платежей", report.totalCount.toString(), Modifier.weight(1f))
        ReportCard("Всего", "${report.totalAmount} ₽", Modifier.weight(1f))
        ReportCard("Оплачено", "${report.paidAmount} ₽", Modifier.weight(1f), AppColors.Success)
        ReportCard("Ожидает", "${report.pendingAmount} ₽", Modifier.weight(1f), AppColors.Warning)
        ReportCard("Ошибки", "${report.failedAmount} ₽", Modifier.weight(1f), MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun ReportCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, color = valueColor)
        }
    }
}

@Composable
private fun PaymentsTable(
    items: List<PaymentUi>,
    onPay: (PaymentUi) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PaymentsTableHeader()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(items) { index, payment ->
                    PaymentTableRow(
                        index = index + 1,
                        payment = payment,
                        onPay = { onPay(payment) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentsTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.TableHeaderBackground)
            .padding(vertical = 14.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("ID", 0.6f)
        HeaderCell("Заказ", 0.8f)
        HeaderCell("Сумма", 1f)
        HeaderCell("Метод", 1.1f)
        HeaderCell("Статус", 1.1f)
        HeaderCell("Дата оплаты", 1.4f)
        HeaderCell("Действие", 1f)
    }
}

@Composable
private fun PaymentTableRow(
    index: Int,
    payment: PaymentUi,
    onPay: () -> Unit
) {
    val defaultBackground = if (index % 2 == 0) AppColors.TableRowEven else AppColors.TableRowOdd
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val background = if (isHovered) MaterialTheme.colorScheme.primary.copy(alpha = 0.06f) else defaultBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Cell(payment.id.toString(), 0.6f)
        Cell("#${payment.orderId}", 0.8f)
        PriceCell(payment.amount, 1f)
        Cell(payment.paymentMethod, 1.1f)
        StatusCell(payment.paymentStatus, 1.1f)
        Cell(payment.paidAt, 1.4f)
        ActionCell(
            canPay = payment.rawPaymentStatus == "PENDING",
            onPay = onPay,
            weight = 1f
        )
    }
}

@Composable
private fun RowScope.HeaderCell(text: String, weight: Float) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.weight(weight).padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.Cell(text: String, weight: Float) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.weight(weight).padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.PriceCell(value: String, weight: Float) {
    Text(
        text = "$value ₽",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.weight(weight).padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.StatusCell(value: String, weight: Float) {
    val color = when (value) {
        "Оплачено" -> AppColors.Success
        "Ожидает" -> AppColors.Warning
        "Ошибка" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }
    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = Modifier.weight(weight).padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.ActionCell(
    canPay: Boolean,
    onPay: () -> Unit,
    weight: Float
) {
    Row(
        modifier = Modifier.weight(weight).padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canPay) {
            Button(onClick = onPay) {
                Text("Подтвердить")
            }
        } else {
            Text("-", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun formatPaymentStatusOrAll(value: String): String {
    return if (value == "Все") value else formatPaymentStatus(value)
}

private fun formatPaymentMethodOrAll(value: String): String {
    return if (value == "Все") value else formatPaymentMethod(value)
}
