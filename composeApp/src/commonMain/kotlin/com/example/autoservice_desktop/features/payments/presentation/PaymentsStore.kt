package com.example.autoservice_desktop.features.payments.presentation

import com.example.autoservice_desktop.core.ui.formatPaymentMethod
import com.example.autoservice_desktop.core.ui.formatPaymentStatus
import com.example.autoservice_desktop.core.ui.formatRuDateTime
import com.example.autoservice_desktop.features.payments.data.CreatePaymentRequest
import com.example.autoservice_desktop.features.payments.data.PaymentDto
import com.example.autoservice_desktop.features.payments.data.PaymentFilters
import com.example.autoservice_desktop.features.payments.data.PaymentReportDto
import com.example.autoservice_desktop.features.payments.data.PaymentsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class PaymentsStore(
    private val repository: PaymentsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(PaymentsState())
    val state: StateFlow<PaymentsState> = _state.asStateFlow()

    fun dispatch(action: PaymentsAction) {
        when (action) {
            PaymentsAction.Load -> loadPayments()
            is PaymentsAction.ChangeStatus -> updateStatus(action.value)
            is PaymentsAction.ChangeMethod -> updateMethod(action.value)
            is PaymentsAction.ChangeFrom -> updateFrom(action.value)
            is PaymentsAction.ChangeTo -> updateTo(action.value)
            PaymentsAction.ClearFilters -> clearFilters()
            PaymentsAction.OpenCreateDialog -> openCreateDialog()
            PaymentsAction.CloseCreateDialog -> closeCreateDialog()
            is PaymentsAction.ChangeCreateOrderId -> updateCreateOrderId(action.value)
            is PaymentsAction.ChangeCreateAmount -> updateCreateAmount(action.value)
            is PaymentsAction.ChangeCreateMethod -> updateCreateMethod(action.value)
            PaymentsAction.SubmitCreate -> submitCreate()
            is PaymentsAction.PayPayment -> payPayment(action.paymentId)
        }
    }

    private fun loadPayments() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching {
                val filters = _state.value.toFilters()
                repository.getPayments(filters) to repository.getPaymentReport(filters)
            }.onSuccess { (payments, report) ->
                _state.value = _state.value.copy(
                    items = payments.map { it.toUi() },
                    report = report.toUi(),
                    isLoading = false,
                    error = null
                )
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    items = emptyList(),
                    report = null,
                    isLoading = false,
                    error = throwable.message ?: "Не удалось загрузить оплаты"
                )
            }
        }
    }

    private fun updateStatus(value: String) {
        _state.value = _state.value.copy(statusFilter = value)
        loadPayments()
    }

    private fun updateMethod(value: String) {
        _state.value = _state.value.copy(methodFilter = value)
        loadPayments()
    }

    private fun updateFrom(value: String) {
        _state.value = _state.value.copy(fromFilter = value)
    }

    private fun updateTo(value: String) {
        _state.value = _state.value.copy(toFilter = value)
    }

    private fun clearFilters() {
        _state.value = _state.value.copy(
            statusFilter = "",
            methodFilter = "",
            fromFilter = "",
            toFilter = ""
        )
        loadPayments()
    }

    private fun openCreateDialog() {
        _state.value = _state.value.copy(
            isCreateDialogOpen = true,
            createForm = CreatePaymentForm()
        )
    }

    private fun closeCreateDialog() {
        _state.value = _state.value.copy(
            isCreateDialogOpen = false,
            createForm = CreatePaymentForm()
        )
    }

    private fun updateCreateOrderId(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(orderId = value, errorMessage = null)
        )
    }

    private fun updateCreateAmount(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(amount = value, errorMessage = null)
        )
    }

    private fun updateCreateMethod(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(paymentMethod = value, errorMessage = null)
        )
    }

    private fun submitCreate() {
        val form = _state.value.createForm
        val orderId = form.orderId.trim().toLongOrNull()
        val amount = form.amount.trim()

        when {
            orderId == null || orderId < 1 -> {
                _state.value = _state.value.copy(createForm = form.copy(errorMessage = "Введите корректный номер заказа"))
                return
            }
            amount.isBlank() -> {
                _state.value = _state.value.copy(createForm = form.copy(errorMessage = "Введите сумму"))
                return
            }
        }

        scope.launch {
            _state.value = _state.value.copy(
                createForm = _state.value.createForm.copy(isSubmitting = true, errorMessage = null)
            )

            runCatching {
                repository.createPayment(
                    orderId = orderId,
                    request = CreatePaymentRequest(
                        amount = amount,
                        paymentMethod = form.paymentMethod
                    )
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    isCreateDialogOpen = false,
                    createForm = CreatePaymentForm()
                )
                loadPayments()
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    createForm = _state.value.createForm.copy(
                        isSubmitting = false,
                        errorMessage = throwable.message ?: "Не удалось создать оплату"
                    )
                )
            }
        }
    }

    private fun payPayment(paymentId: Long) {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching {
                repository.payPayment(paymentId)
            }.onSuccess {
                loadPayments()
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = throwable.message ?: "Не удалось подтвердить оплату"
                )
            }
        }
    }

    private fun PaymentDto.toUi(): PaymentUi {
        return PaymentUi(
            id = id,
            orderId = orderId,
            amount = amount,
            paymentMethod = formatPaymentMethod(paymentMethod),
            paymentStatus = formatPaymentStatus(paymentStatus),
            rawPaymentMethod = paymentMethod,
            rawPaymentStatus = paymentStatus,
            paidAt = paidAt?.let(::formatRuDateTime) ?: "-"
        )
    }

    private fun PaymentReportDto.toUi(): PaymentReportUi {
        return PaymentReportUi(
            totalCount = totalCount,
            totalAmount = totalAmount,
            paidAmount = paidAmount,
            pendingAmount = pendingAmount,
            failedAmount = failedAmount
        )
    }

    private fun PaymentsState.toFilters(): PaymentFilters {
        return PaymentFilters(
            status = statusFilter.ifBlank { null },
            method = methodFilter.ifBlank { null },
            from = fromFilter.trim().ifBlank { null },
            to = toFilter.trim().ifBlank { null }
        )
    }
}
