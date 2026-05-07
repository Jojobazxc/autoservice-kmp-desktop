package com.example.autoservice_desktop.features.auth.data

internal data class AuthSession(
    val token: String,
    val tokenType: String,
    val expiresAtMillis: Long,
    val role: UserRole
)
