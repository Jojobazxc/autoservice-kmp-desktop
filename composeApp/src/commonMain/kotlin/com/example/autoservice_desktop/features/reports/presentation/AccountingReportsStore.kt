package com.example.autoservice_desktop.features.reports.presentation

import com.example.autoservice_desktop.core.ui.formatOrderStatus
import com.example.autoservice_desktop.core.ui.formatPaymentMethod
import com.example.autoservice_desktop.core.ui.formatPaymentStatus
import com.example.autoservice_desktop.core.ui.formatRuDateTime
import com.example.autoservice_desktop.features.clients.data.ClientsRepository
import com.example.autoservice_desktop.features.payments.data.AccountingDebtItemDto
import com.example.autoservice_desktop.features.payments.data.AccountingDebtsPageDto
import com.example.autoservice_desktop.features.payments.data.AccountingDebtsTotalsDto
import com.example.autoservice_desktop.features.payments.data.AccountingPaymentItemDto
import com.example.autoservice_desktop.features.payments.data.AccountingPaymentsPageDto
import com.example.autoservice_desktop.features.payments.data.AccountingPaymentsTotalsDto
import com.example.autoservice_desktop.features.payments.data.AccountingReportFilters
import com.example.autoservice_desktop.features.payments.data.AccountingReportsRepository
import com.example.autoservice_desktop.features.payments.data.AccountingSummaryDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class AccountingReportsStore(
    private val repository: AccountingReportsRepository,
    private val clientsRepository: ClientsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(AccountingReportsState())
    val state: StateFlow<AccountingReportsState> = _state.asStateFlow()

    fun dispatch(action: AccountingReportsAction) {
        when (action) {
            AccountingReportsAction.LoadClients -> loadClients()
            is AccountingReportsAction.ChangeReportType -> changeReportType(action.value)
            is AccountingReportsAction.ChangeFrom -> _state.value = _state.value.copy(fromFilter = action.value)
            is AccountingReportsAction.ChangeTo -> _state.value = _state.value.copy(toFilter = action.value)
            is AccountingReportsAction.ChangeStatus -> _state.value = _state.value.copy(statusFilter = action.value)
            is AccountingReportsAction.ChangeMethod -> _state.value = _state.value.copy(methodFilter = action.value)
            is AccountingReportsAction.ChangeClientId -> _state.value = _state.value.copy(clientIdFilter = action.value)
            is AccountingReportsAction.ChangeOrderId -> _state.value = _state.value.copy(orderIdFilter = action.value)
            is AccountingReportsAction.ChangeOrderStatus -> _state.value = _state.value.copy(orderStatusFilter = action.value)
            AccountingReportsAction.Generate -> generate()
            AccountingReportsAction.ClearFilters -> clearFilters()
            AccountingReportsAction.NextPage -> changePage(1)
            AccountingReportsAction.PreviousPage -> changePage(-1)
        }
    }

    private fun changeReportType(value: AccountingReportType) {
        _state.value = _state.value.copy(
            reportType = value,
            clientIdFilter = if (value == AccountingReportType.Summary) "" else _state.value.clientIdFilter,
            orderIdFilter = if (value == AccountingReportType.Payments) _state.value.orderIdFilter else "",
            statusFilter = if (value == AccountingReportType.Payments) _state.value.statusFilter else "",
            methodFilter = if (value == AccountingReportType.Payments) _state.value.methodFilter else "",
            orderStatusFilter = if (value == AccountingReportType.Debts) _state.value.orderStatusFilter else "",
            payments = _state.value.payments.copy(page = 1),
            debts = _state.value.debts.copy(page = 1)
        )
    }

    private fun loadClients() {
        if (_state.value.clientOptions.isNotEmpty()) return

        scope.launch {
            runCatching {
                clientsRepository.getClients()
            }.onSuccess { clients ->
                _state.value = _state.value.copy(
                    clientOptions = clients.map {
                        AccountingClientOptionUi(
                            id = it.id,
                            title = "#${it.id} ${it.fullName}"
                        )
                    }
                )
            }.onFailure {
                // Client list is an optional filter helper; report generation must still work.
            }
        }
    }

    private fun generate() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching {
                val filters = _state.value.toFilters()
                when (_state.value.reportType) {
                    AccountingReportType.Summary -> GeneratedReport(
                        summary = repository.getSummary(filters)
                    )
                    AccountingReportType.Payments -> GeneratedReport(
                        payments = repository.getPayments(filters)
                    )
                    AccountingReportType.Debts -> GeneratedReport(
                        debts = repository.getDebts(filters)
                    )
                }
            }.onSuccess { report ->
                _state.value = _state.value.copy(
                    summary = report.summary?.toUi(),
                    payments = report.payments?.toUi() ?: _state.value.payments.copy(items = emptyList(), total = 0),
                    debts = report.debts?.toUi() ?: _state.value.debts.copy(items = emptyList(), total = 0),
                    isLoading = false,
                    hasGeneratedReport = true,
                    error = null
                )
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = throwable.message ?: "Не удалось сформировать отчет"
                )
            }
        }
    }

    private fun clearFilters() {
        _state.value = AccountingReportsState(
            reportType = _state.value.reportType,
            clientOptions = _state.value.clientOptions
        )
    }

    private fun changePage(delta: Int) {
        val state = _state.value
        when (state.reportType) {
            AccountingReportType.Summary -> return
            AccountingReportType.Payments -> {
                val next = (state.payments.page + delta).coerceIn(1, state.payments.maxPage())
                if (next == state.payments.page) return
                _state.value = state.copy(payments = state.payments.copy(page = next))
            }
            AccountingReportType.Debts -> {
                val next = (state.debts.page + delta).coerceIn(1, state.debts.maxPage())
                if (next == state.debts.page) return
                _state.value = state.copy(debts = state.debts.copy(page = next))
            }
        }
        generate()
    }

    private fun AccountingReportsState.toFilters(): AccountingReportFilters {
        return AccountingReportFilters(
            from = fromFilter.trim().ifBlank { null },
            to = toFilter.trim().ifBlank { null },
            status = statusFilter.ifBlank { null },
            method = methodFilter.ifBlank { null },
            clientId = clientIdFilter.trim().toLongOrNull(),
            orderId = orderIdFilter.trim().toLongOrNull(),
            orderStatus = orderStatusFilter.ifBlank { null },
            paymentsPage = payments.page,
            debtsPage = debts.page,
            limit = payments.limit
        )
    }

    private fun AccountingSummaryDto.toUi(): AccountingSummaryUi {
        return AccountingSummaryUi(
            revenue = totals.revenue,
            paid = totals.paid,
            unpaid = totals.unpaid,
            ordersCount = totals.ordersCount,
            byStatus = byStatus.map {
                AccountingStatusUi(
                    status = formatPaymentStatus(it.status),
                    amount = it.amount,
                    count = it.count
                )
            }
        )
    }

    private fun AccountingPaymentsPageDto.toUi(): AccountingPaymentsPageUi {
        return AccountingPaymentsPageUi(
            items = items.map { it.toUi() },
            page = page,
            limit = limit,
            total = total,
            totals = totals.toUi()
        )
    }

    private fun AccountingPaymentsTotalsDto.toUi(): AccountingPaymentsTotalsUi {
        return AccountingPaymentsTotalsUi(
            amount = amount,
            count = count,
            paidAmount = paidAmount,
            pendingAmount = pendingAmount,
            failedAmount = failedAmount
        )
    }

    private fun AccountingPaymentItemDto.toUi(): AccountingPaymentUi {
        return AccountingPaymentUi(
            paymentId = paymentId,
            orderId = orderId,
            clientName = clientName,
            car = car,
            amount = amount,
            status = formatPaymentStatus(status),
            method = formatPaymentMethod(method),
            paidAt = paidAt?.let(::formatRuDateTime) ?: "-"
        )
    }

    private fun AccountingDebtsPageDto.toUi(): AccountingDebtsPageUi {
        return AccountingDebtsPageUi(
            items = items.map { it.toUi() },
            page = page,
            limit = limit,
            total = total,
            totals = totals.toUi()
        )
    }

    private fun AccountingDebtsTotalsDto.toUi(): AccountingDebtsTotalsUi {
        return AccountingDebtsTotalsUi(
            totalAmount = totalAmount,
            paidAmount = paidAmount,
            debtAmount = debtAmount,
            ordersCount = ordersCount
        )
    }

    private fun AccountingDebtItemDto.toUi(): AccountingDebtUi {
        return AccountingDebtUi(
            orderId = orderId,
            clientName = clientName,
            car = car,
            totalAmount = totalAmount,
            paidAmount = paidAmount,
            debtAmount = debtAmount,
            orderStatus = formatOrderStatus(orderStatus),
            lastPaymentAt = lastPaymentAt?.let(::formatRuDateTime) ?: "-"
        )
    }

    private fun AccountingPaymentsPageUi.maxPage(): Int {
        return ((total + limit - 1) / limit).coerceAtLeast(1)
    }

    private fun AccountingDebtsPageUi.maxPage(): Int {
        return ((total + limit - 1) / limit).coerceAtLeast(1)
    }

    private data class GeneratedReport(
        val summary: AccountingSummaryDto? = null,
        val payments: AccountingPaymentsPageDto? = null,
        val debts: AccountingDebtsPageDto? = null
    )
}
