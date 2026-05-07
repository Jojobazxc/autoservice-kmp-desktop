package com.example.autoservice_desktop.features.auth.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class AuthSessionManager {
    private val _session = MutableStateFlow<AuthSession?>(null)
    val session: StateFlow<AuthSession?> = _session.asStateFlow()

    fun currentSession(): AuthSession? = _session.value

    fun update(loginResponse: LoginResponse) {
        _session.value = AuthSession(
            token = loginResponse.token,
            tokenType = loginResponse.tokenType,
            expiresAtMillis = currentTimeMillis() + loginResponse.expiresInMillis,
            role = loginResponse.role
        )
    }

    fun clear() {
        _session.value = null
    }
}

internal expect fun currentTimeMillis(): Long
