package com.example.autoservice_desktop.features.auth.presentation

import com.example.autoservice_desktop.core.network.ForbiddenApiException
import com.example.autoservice_desktop.core.network.UnauthorizedApiException
import com.example.autoservice_desktop.features.auth.data.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class AuthStore(
    private val repository: AuthRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun dispatch(action: AuthAction) {
        when (action) {
            is AuthAction.ChangeLogin -> updateLogin(action.value)
            is AuthAction.ChangePassword -> updatePassword(action.value)
            AuthAction.SubmitLogin -> submitLogin()
            AuthAction.Logout -> logout()
        }
    }

    private fun updateLogin(value: String) {
        _state.value = _state.value.copy(login = value, error = null)
    }

    private fun updatePassword(value: String) {
        _state.value = _state.value.copy(password = value, error = null)
    }

    private fun submitLogin() {
        val login = _state.value.login.trim()
        val password = _state.value.password

        if (login.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "Заполните логин и пароль")
            return
        }

        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching {
                repository.login(login, password)
            }.onSuccess {
                _state.value = AuthState()
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = throwable.toAuthMessage()
                )
            }
        }
    }

    private fun logout() {
        repository.logout()
        _state.value = AuthState()
    }

    private fun Throwable.toAuthMessage(): String {
        return when (this) {
            is UnauthorizedApiException -> "Неверный логин или пароль"
            is ForbiddenApiException -> "Нет доступа"
            else -> message ?: "Не удалось войти"
        }
    }
}
