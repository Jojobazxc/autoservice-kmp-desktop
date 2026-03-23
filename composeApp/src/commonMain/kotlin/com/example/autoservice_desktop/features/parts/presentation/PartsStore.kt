package com.example.autoservice_desktop.features.parts.presentation

import com.example.autoservice_desktop.features.parts.data.CreatePartRequest
import com.example.autoservice_desktop.features.parts.data.PartsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class PartsStore(
    private val repository: PartsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(PartsState())
    internal val state: StateFlow<PartsState> = _state.asStateFlow()

    internal fun dispatch(action: PartsAction) {
        when (action) {
            PartsAction.Load -> loadParts()

            PartsAction.OpenCreateDialog -> openCreateDialog()
            PartsAction.CloseCreateDialog -> closeCreateDialog()
            is PartsAction.ChangeCreateName -> changeCreateName(action.value)
            is PartsAction.ChangeCreateArticle -> changeCreateArticle(action.value)
            is PartsAction.ChangeCreatePrice -> changeCreatePrice(action.value)
            is PartsAction.ChangeCreateUnit -> changeCreateUnit(action.value)
            is PartsAction.ChangeCreateStockQuantity -> changeCreateStockQuantity(action.value)
            PartsAction.SubmitCreate -> submitCreate()
        }
    }

    private fun loadParts() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching { repository.getParts() }
                .onSuccess { parts ->
                    _state.value = _state.value.copy(
                        items = parts,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        items = emptyList(),
                        isLoading = false,
                        error = throwable.message ?: "Неизвестная ошибка"
                    )
                }
        }
    }

    private fun openCreateDialog() {
        _state.value = _state.value.copy(
            isCreateDialogOpen = true,
            createForm = CreatePartForm()
        )
    }

    private fun closeCreateDialog() {
        _state.value = _state.value.copy(
            isCreateDialogOpen = false,
            createForm = CreatePartForm()
        )
    }

    private fun changeCreateName(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(name = value, errorMessage = null)
        )
    }

    private fun changeCreateArticle(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(article = value, errorMessage = null)
        )
    }

    private fun changeCreatePrice(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(price = value, errorMessage = null)
        )
    }

    private fun changeCreateUnit(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(unit = value, errorMessage = null)
        )
    }

    private fun changeCreateStockQuantity(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(stockQuantity = value, errorMessage = null)
        )
    }

    private fun submitCreate() {
        val form = _state.value.createForm
        val stock = form.stockQuantity.toIntOrNull()

        when {
            form.name.isBlank() -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Введи название")
                )
                return
            }

            form.article.isBlank() -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Введи артикул")
                )
                return
            }

            form.price.isBlank() -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Введи цену")
                )
                return
            }

            form.unit.isBlank() -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Введи единицу измерения")
                )
                return
            }

            stock == null || stock < 0 -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Остаток должен быть 0 или больше")
                )
                return
            }
        }

        scope.launch {
            _state.value = _state.value.copy(
                createForm = form.copy(isSubmitting = true, errorMessage = null)
            )

            runCatching {
                repository.createPart(
                    CreatePartRequest(
                        name = form.name.trim(),
                        article = form.article.trim(),
                        price = form.price.trim(),
                        unit = form.unit.trim(),
                        stockQuantity = stock
                    )
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    isCreateDialogOpen = false,
                    createForm = CreatePartForm()
                )
                loadParts()
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    createForm = _state.value.createForm.copy(
                        isSubmitting = false,
                        errorMessage = throwable.message ?: "Не удалось добавить запчасть"
                    )
                )
            }
        }
    }
}