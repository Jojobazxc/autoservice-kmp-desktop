package com.example.autoservice_desktop.core.network

internal open class ApiException(message: String) : RuntimeException(message)

internal class UnauthorizedApiException : ApiException("Требуется повторный вход")

internal class ForbiddenApiException : ApiException("Нет доступа")
