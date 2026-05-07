package com.example.autoservice_desktop.features.auth.data

internal class AuthRepository(
    private val api: AuthApi,
    private val sessionManager: AuthSessionManager
) {
    suspend fun login(login: String, password: String): AuthSession {
        val response = api.login(
            LoginRequest(
                login = login,
                password = password
            )
        )
        sessionManager.update(response)
        return requireNotNull(sessionManager.currentSession())
    }

    fun logout() {
        sessionManager.clear()
    }
}
