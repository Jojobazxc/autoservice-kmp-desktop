package com.example.autoservice_desktop.features.auth.data

import kotlinx.serialization.Serializable

@Serializable
internal data class LoginRequest(
    val login: String,
    val password: String
)

@Serializable
internal data class LoginResponse(
    val token: String,
    val tokenType: String,
    val expiresInMillis: Long,
    val role: UserRole
)

@Serializable
internal enum class UserRole {
    ADMIN,
    MANAGER,
    MECHANIC,
    ACCOUNTANT
}
