package com.example.autoservice_desktop.features.clients.presentation

import com.example.autoservice_desktop.features.clients.data.ClientsRepository
import com.example.autoservice_desktop.features.clients.data.CreateClientRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class ClientsStore(
    private val repository: ClientsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(ClientsState())
    val state: StateFlow<ClientsState> = _state.asStateFlow()

    fun dispatch(action: ClientsAction) {
        when (action) {
            ClientsAction.Load -> loadClients()
            ClientsAction.OpenCreateDialog -> openCreateDialog()
            ClientsAction.CloseCreateDialog -> closeCreateDialog()
            is ClientsAction.UpdateFullName -> updateFullName(action.value)
            is ClientsAction.UpdatePhone -> updatePhone(action.value)
            is ClientsAction.UpdateEmail -> updateEmail(action.value)
            is ClientsAction.UpdateAddress -> updateAddress(action.value)
            is ClientsAction.UpdateStatus -> updateStatus(action.value)
            ClientsAction.SubmitCreate -> submitCreate()
        }
    }

    private fun loadClients() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching { repository.getClients() }
                .onSuccess { clients ->
                    _state.value = _state.value.copy(
                        items = clients,
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
            createError = null
        )
    }

    private fun closeCreateDialog() {
        _state.value = _state.value.copy(
            isCreateDialogOpen = false,
            isCreating = false,
            fullNameInput = "",
            phoneInput = "",
            emailInput = "",
            addressInput = "",
            statusInput = "REGULAR",
            createError = null
        )
    }

    private fun updateFullName(value: String) {
        _state.value = _state.value.copy(fullNameInput = value)
    }

    private fun updatePhone(value: String) {
        _state.value = _state.value.copy(phoneInput = value)
    }

    private fun updateEmail(value: String) {
        _state.value = _state.value.copy(emailInput = value)
    }

    private fun updateAddress(value: String) {
        _state.value = _state.value.copy(addressInput = value)
    }

    private fun updateStatus(value: String) {
        _state.value = _state.value.copy(statusInput = value)
    }

    private fun submitCreate() {
        val current = _state.value

        val fullName = current.fullNameInput.trim()
        val phone = current.phoneInput.trim()
        val email = current.emailInput.trim().ifBlank { null }
        val address = current.addressInput.trim().ifBlank { null }
        val status = current.statusInput

        if (fullName.isBlank()) {
            _state.value = current.copy(createError = "Введите ФИО")
            return
        }

        if (phone.isBlank()) {
            _state.value = current.copy(createError = "Введите телефон")
            return
        }

        scope.launch {
            _state.value = _state.value.copy(
                isCreating = true,
                createError = null
            )

            runCatching {
                repository.createClient(
                    CreateClientRequest(
                        fullName = fullName,
                        phone = phone,
                        email = email,
                        address = address,
                        status = status
                    )
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    isCreateDialogOpen = false,
                    isCreating = false,
                    fullNameInput = "",
                    phoneInput = "",
                    emailInput = "",
                    addressInput = "",
                    statusInput = "REGULAR",
                    createError = null
                )
                loadClients()
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isCreating = false,
                    createError = throwable.message ?: "Не удалось создать клиента"
                )
            }
        }
    }
}