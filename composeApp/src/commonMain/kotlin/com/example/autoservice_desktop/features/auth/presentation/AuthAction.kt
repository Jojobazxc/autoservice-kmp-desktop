package com.example.autoservice_desktop.features.auth.presentation

internal sealed interface AuthAction {
    data class ChangeLogin(val value: String) : AuthAction
    data class ChangePassword(val value: String) : AuthAction
    data object SubmitLogin : AuthAction
    data object Logout : AuthAction
}
