package com.example.autoservice_desktop.features.auth.presentation

internal data class AuthState(
    val login: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
