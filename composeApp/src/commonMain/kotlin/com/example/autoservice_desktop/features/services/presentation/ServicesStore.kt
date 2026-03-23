package com.example.autoservice_desktop.features.services.presentation

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
        }
    }

    private fun loadServices() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching { repository.getServices() }
                .onSuccess { services ->
                    _state.value = ServicesState(
                        items = services,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { throwable ->
                    _state.value = ServicesState(
                        items = emptyList(),
                        isLoading = false,
                        error = throwable.message ?: "Неизвестная ошибка"
                    )
                }
        }
    }
}