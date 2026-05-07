package com.example.autoservice_desktop.features.reports.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autoservice_desktop.core.ui.AppDatePickerField
import com.example.autoservice_desktop.core.ui.AppDropdownField
import com.example.autoservice_desktop.core.ui.formatOrderStatus
import com.example.autoservice_desktop.core.ui.formatPaymentMethod
import com.example.autoservice_desktop.core.ui.formatPaymentStatus
import com.example.autoservice_desktop.core.ui.theme.AppColors
import com.example.autoservice_desktop.features.reports.presentation.AccountingDebtUi
import com.example.autoservice_desktop.features.reports.presentation.AccountingDebtsPageUi
import com.example.autoservice_desktop.features.reports.presentation.AccountingDebtsTotalsUi
import com.example.autoservice_desktop.features.reports.presentation.AccountingPaymentUi
import com.example.autoservice_desktop.features.reports.presentation.AccountingPaymentsPageUi
import com.example.autoservice_desktop.features.reports.presentation.AccountingPaymentsTotalsUi
import com.example.autoservice_desktop.features.reports.presentation.AccountingReportType
import com.example.autoservice_desktop.features.reports.presentation.AccountingReportsAction
import com.example.autoservice_desktop.features.reports.presentation.AccountingReportsState
import com.example.autoservice_desktop.features.reports.presentation.AccountingReportsStore
import com.example.autoservice_desktop.features.reports.presentation.AccountingSummaryUi

@Composable
internal fun AccountingReportsScreen(
    store: AccountingReportsStore
) {
    val state by store.state.collectAsState()

    LaunchedEffect(Unit) {
        store.dispatch(AccountingReportsAction.LoadClients)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Отчеты",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        ReportsParametersPanel(
            state = state,
            onAction = store::dispatch
        )

        when {
            state.isLoading -> Text("Формирование отчета...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            state.error != null -> Text("Ошибка: ${state.error}", color = MaterialTheme.colorScheme.error)
            !state.hasGeneratedReport -> Text("Задайте параметры и нажмите «Сформировать».", color = MaterialTheme.colorScheme.onSurfaceVariant)
            else -> ReportResult(
                state = state,
                onAction = store::dispatch
            )
        }
    }
}

@Composable
private fun ReportsParametersPanel(
    state: AccountingReportsState,
    onAction: (AccountingReportsAction) -> Unit
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
                    label = "Тип отчета",
                    selectedValue = state.reportType.name,
                    options = AccountingReportType.entries.map { it.name },
                    optionLabel = { it.toReportTypeLabel() },
                    onValueSelected = { raw ->
                        AccountingReportType.entries
                            .firstOrNull { it.name == raw }
                            ?.let { onAction(AccountingReportsAction.ChangeReportType(it)) }
                    },
                    modifier = Modifier.weight(1f)
                )

                AppDatePickerField(
                    label = "С даты",
                    isoDate = state.fromFilter,
                    onDateSelected = { onAction(AccountingReportsAction.ChangeFrom(it)) },
                    modifier = Modifier.weight(1f)
                )

                AppDatePickerField(
                    label = "По дату",
                    isoDate = state.toFilter,
                    onDateSelected = { onAction(AccountingReportsAction.ChangeTo(it)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.reportType == AccountingReportType.Payments) {
                    ClientFilter(
                        state = state,
                        onAction = onAction,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = state.orderIdFilter,
                        onValueChange = { onAction(AccountingReportsAction.ChangeOrderId(it)) },
                        label = { Text("ID заказа") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    AppDropdownField(
                        label = "Статус оплаты",
                        selectedValue = state.statusFilter,
                        options = listOf("", "PENDING", "PAID", "FAILED"),
                        optionLabel = { value -> value.ifBlank { "Все" }.let(::formatPaymentStatusOrAll) },
                        onValueSelected = { onAction(AccountingReportsAction.ChangeStatus(it)) },
                        modifier = Modifier.weight(1f)
                    )

                    AppDropdownField(
                        label = "Метод",
                        selectedValue = state.methodFilter,
                        options = listOf("", "CASH", "CARD"),
                        optionLabel = { value -> value.ifBlank { "Все" }.let(::formatPaymentMethodOrAll) },
                        onValueSelected = { onAction(AccountingReportsAction.ChangeMethod(it)) },
                        modifier = Modifier.weight(1f)
                    )
                } else if (state.reportType == AccountingReportType.Debts) {
                    ClientFilter(
                        state = state,
                        onAction = onAction,
                        modifier = Modifier.weight(1f)
                    )

                    AppDropdownField(
                        label = "Статус заказа",
                        selectedValue = state.orderStatusFilter,
                        options = listOf("", "CREATED", "IN_PROGRESS", "COMPLETED", "PAID", "CANCELED"),
                        optionLabel = { value -> value.ifBlank { "Все" }.let(::formatOrderStatusOrAll) },
                        onValueSelected = { onAction(AccountingReportsAction.ChangeOrderStatus(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = { onAction(AccountingReportsAction.Generate) },
                    modifier = Modifier.widthIn(min = 132.dp)
                ) {
                    Text("Сформировать")
                }

                OutlinedButton(
                    onClick = { onAction(AccountingReportsAction.ClearFilters) },
                    modifier = Modifier.widthIn(min = 116.dp)
                ) {
                    Text("Сбросить")
                }
            }
        }
    }
}

@Composable
private fun ClientFilter(
    state: AccountingReportsState,
    onAction: (AccountingReportsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    AppDropdownField(
        label = "Клиент",
        selectedValue = state.clientIdFilter.ifBlank { "all" },
        options = listOf("all") + state.clientOptions.map { it.id.toString() },
        optionLabel = { rawId ->
            when (rawId) {
                "all" -> "Все"
                else -> state.clientOptions
                    .firstOrNull { it.id.toString() == rawId }
                    ?.title
                    ?: rawId
            }
        },
        onValueSelected = { rawId ->
            onAction(AccountingReportsAction.ChangeClientId(rawId.takeUnless { it == "all" }.orEmpty()))
        },
        modifier = modifier
    )
}

@Composable
private fun ReportResult(
    state: AccountingReportsState,
    onAction: (AccountingReportsAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        when (state.reportType) {
            AccountingReportType.Summary -> {
                state.summary?.let {
                    SummaryCards(it)
                    SummaryByStatus(it)
                }
            }
            AccountingReportType.Payments -> {
                state.payments.totals?.let { PaymentsTotalsCards(it) }
                PaymentsReportTable(
                    page = state.payments,
                    onAction = onAction
                )
            }
            AccountingReportType.Debts -> {
                state.debts.totals?.let { DebtsTotalsCards(it) }
                DebtsReportTable(
                    page = state.debts,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun SummaryCards(summary: AccountingSummaryUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ReportCard("Выручка", "${summary.revenue} ₽", Modifier.weight(1f))
        ReportCard("Оплачено", "${summary.paid} ₽", Modifier.weight(1f), AppColors.Success)
        ReportCard("Задолженность", "${summary.unpaid} ₽", Modifier.weight(1f), AppColors.Warning)
        ReportCard("Заказов", summary.ordersCount.toString(), Modifier.weight(1f))
    }
}

@Composable
private fun PaymentsTotalsCards(totals: AccountingPaymentsTotalsUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ReportCard("Сумма", "${totals.amount} ₽", Modifier.weight(1f))
        ReportCard("Платежей", totals.count.toString(), Modifier.weight(1f))
        ReportCard("Оплачено", "${totals.paidAmount} ₽", Modifier.weight(1f), AppColors.Success)
        ReportCard("Ожидает", "${totals.pendingAmount} ₽", Modifier.weight(1f), AppColors.Warning)
        ReportCard("Ошибки", "${totals.failedAmount} ₽", Modifier.weight(1f), MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun DebtsTotalsCards(totals: AccountingDebtsTotalsUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ReportCard("Сумма заказов", "${totals.totalAmount} ₽", Modifier.weight(1f))
        ReportCard("Оплачено", "${totals.paidAmount} ₽", Modifier.weight(1f), AppColors.Success)
        ReportCard("Задолженность", "${totals.debtAmount} ₽", Modifier.weight(1f), AppColors.Warning)
        ReportCard("Заказов", totals.ordersCount.toString(), Modifier.weight(1f))
    }
}

@Composable
private fun SummaryByStatus(summary: AccountingSummaryUi) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            TableHeader {
                HeaderCell("Статус", 1f)
                HeaderCell("Сумма", 1f)
                HeaderCell("Количество", 1f)
            }
            summary.byStatus.forEachIndexed { index, row ->
                TableRow(index) {
                    Cell(row.status, 1f)
                    PriceCell(row.amount, 1f)
                    Cell(row.count.toString(), 1f)
                }
            }
        }
    }
}

@Composable
private fun PaymentsReportTable(
    page: AccountingPaymentsPageUi,
    onAction: (AccountingReportsAction) -> Unit
) {
    ReportTableFrame(
        footer = {
            PaginationFooter(
                page = page.page,
                limit = page.limit,
                total = page.total,
                onPrevious = { onAction(AccountingReportsAction.PreviousPage) },
                onNext = { onAction(AccountingReportsAction.NextPage) }
            )
        }
    ) {
        TableHeader {
            HeaderCell("Платеж", 0.7f)
            HeaderCell("Заказ", 0.7f)
            HeaderCell("Клиент", 1.4f)
            HeaderCell("Авто", 1.2f)
            HeaderCell("Сумма", 0.9f)
            HeaderCell("Статус", 0.9f)
            HeaderCell("Метод", 0.9f)
            HeaderCell("Дата", 1.2f)
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(page.items) { index, payment ->
                PaymentRow(index, payment)
            }
        }
    }
}

@Composable
private fun DebtsReportTable(
    page: AccountingDebtsPageUi,
    onAction: (AccountingReportsAction) -> Unit
) {
    ReportTableFrame(
        footer = {
            PaginationFooter(
                page = page.page,
                limit = page.limit,
                total = page.total,
                onPrevious = { onAction(AccountingReportsAction.PreviousPage) },
                onNext = { onAction(AccountingReportsAction.NextPage) }
            )
        }
    ) {
        TableHeader {
            HeaderCell("Заказ", 0.7f)
            HeaderCell("Клиент", 1.4f)
            HeaderCell("Авто", 1.2f)
            HeaderCell("Всего", 0.9f)
            HeaderCell("Оплачено", 0.9f)
            HeaderCell("Долг", 0.9f)
            HeaderCell("Статус", 1f)
            HeaderCell("Посл. платеж", 1.2f)
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(page.items) { index, debt ->
                DebtRow(index, debt)
            }
        }
    }
}

@Composable
private fun PaymentRow(index: Int, payment: AccountingPaymentUi) {
    TableRow(index) {
        Cell("#${payment.paymentId}", 0.7f)
        Cell("#${payment.orderId}", 0.7f)
        Cell(payment.clientName, 1.4f)
        Cell(payment.car, 1.2f)
        PriceCell(payment.amount, 0.9f)
        Cell(payment.status, 0.9f)
        Cell(payment.method, 0.9f)
        Cell(payment.paidAt, 1.2f)
    }
}

@Composable
private fun DebtRow(index: Int, debt: AccountingDebtUi) {
    TableRow(index) {
        Cell("#${debt.orderId}", 0.7f)
        Cell(debt.clientName, 1.4f)
        Cell(debt.car, 1.2f)
        PriceCell(debt.totalAmount, 0.9f)
        PriceCell(debt.paidAmount, 0.9f)
        PriceCell(debt.debtAmount, 0.9f, AppColors.Warning)
        Cell(debt.orderStatus, 1f)
        Cell(debt.lastPaymentAt, 1.2f)
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
private fun ReportTableFrame(
    footer: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(1f)) {
                content()
            }
            footer()
        }
    }
}

@Composable
private fun TableHeader(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.TableHeaderBackground)
            .padding(vertical = 14.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun TableRow(index: Int, content: @Composable RowScope.() -> Unit) {
    val background = if (index % 2 == 0) AppColors.TableRowOdd else AppColors.TableRowEven
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun PaginationFooter(
    page: Int,
    limit: Int,
    total: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val maxPage = ((total + limit - 1) / limit).coerceAtLeast(1)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Всего: $total, страница $page из $maxPage",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 12.dp)
        )
        OutlinedButton(onClick = onPrevious, enabled = page > 1) {
            Text("Назад")
        }
        OutlinedButton(onClick = onNext, enabled = page < maxPage, modifier = Modifier.padding(start = 8.dp)) {
            Text("Вперед")
        }
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
private fun RowScope.PriceCell(
    value: String,
    weight: Float,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text = "$value ₽",
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = Modifier.weight(weight).padding(end = 8.dp)
    )
}

private fun String.toReportTypeLabel(): String {
    return when (this) {
        AccountingReportType.Summary.name -> "Сводка"
        AccountingReportType.Payments.name -> "Платежи"
        AccountingReportType.Debts.name -> "Задолженности"
        else -> this
    }
}

private fun formatPaymentStatusOrAll(value: String): String {
    return if (value == "Все") value else formatPaymentStatus(value)
}

private fun formatPaymentMethodOrAll(value: String): String {
    return if (value == "Все") value else formatPaymentMethod(value)
}

private fun formatOrderStatusOrAll(value: String): String {
    return if (value == "Все") value else formatOrderStatus(value)
}
