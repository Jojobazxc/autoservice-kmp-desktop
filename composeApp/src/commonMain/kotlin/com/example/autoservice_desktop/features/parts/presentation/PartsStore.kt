package com.example.autoservice_desktop.features.parts.presentation

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
        }
    }

    private fun loadParts() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching { repository.getParts() }
                .onSuccess { parts ->
                    _state.value = PartsState(
                        items = parts,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { throwable ->
                    _state.value = PartsState(
                        items = emptyList(),
                        isLoading = false,
                        error = throwable.message ?: "Неизвестная ошибка"
                    )
                }
        }
    }
}