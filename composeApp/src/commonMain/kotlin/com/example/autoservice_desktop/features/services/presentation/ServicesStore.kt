package com.example.autoservice_desktop.features.services.presentation

import com.example.autoservice_desktop.features.services.data.CreateServiceRequest
import com.example.autoservice_desktop.features.services.data.ServicesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class ServicesStore(
    private val repository: ServicesRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(ServicesState())
    internal val state: StateFlow<ServicesState> = _state.asStateFlow()

    internal fun dispatch(action: ServicesAction) {
        when (action) {
            ServicesAction.Load -> loadServices()

            ServicesAction.OpenCreateDialog -> openCreateDialog()
            ServicesAction.CloseCreateDialog -> closeCreateDialog()
            is ServicesAction.ChangeCreateName -> changeCreateName(action.value)
            is ServicesAction.ChangeCreateDescription -> changeCreateDescription(action.value)
            is ServicesAction.ChangeCreateBasePrice -> changeCreateBasePrice(action.value)
            is ServicesAction.ChangeCreateNormHours -> changeCreateNormHours(action.value)
            ServicesAction.SubmitCreate -> submitCreate()
        }
    }

    private fun loadServices() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching { repository.getServices() }
                .onSuccess { services ->
                    _state.value = _state.value.copy(
                        items = services,
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
            createForm = CreateServiceForm()
        )
    }

    private fun closeCreateDialog() {
        _state.value = _state.value.copy(
            isCreateDialogOpen = false,
            createForm = CreateServiceForm()
        )
    }

    private fun changeCreateName(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(name = value, errorMessage = null)
        )
    }

    private fun changeCreateDescription(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(description = value, errorMessage = null)
        )
    }

    private fun changeCreateBasePrice(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(basePrice = value, errorMessage = null)
        )
    }

    private fun changeCreateNormHours(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(normHours = value, errorMessage = null)
        )
    }

    private fun submitCreate() {
        val form = _state.value.createForm

        when {
            form.name.isBlank() -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Введи название услуги")
                )
                return
            }

            form.basePrice.isBlank() -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Введи базовую цену")
                )
                return
            }
        }

        scope.launch {
            _state.value = _state.value.copy(
                createForm = form.copy(isSubmitting = true, errorMessage = null)
            )

            runCatching {
                repository.createService(
                    CreateServiceRequest(
                        name = form.name.trim(),
                        description = form.description.trim().ifBlank { null },
                        basePrice = form.basePrice.trim(),
                        normHours = form.normHours.trim().ifBlank { null }
                    )
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    isCreateDialogOpen = false,
                    createForm = CreateServiceForm()
                )
                loadServices()
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    createForm = _state.value.createForm.copy(
                        isSubmitting = false,
                        errorMessage = throwable.message ?: "Не удалось добавить услугу"
                    )
                )
            }
        }
    }
}