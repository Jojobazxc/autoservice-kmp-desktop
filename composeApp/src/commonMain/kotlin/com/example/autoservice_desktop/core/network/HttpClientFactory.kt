package com.example.autoservice_desktop.core.network

import com.example.autoservice_desktop.features.auth.data.AuthSessionManager
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

internal expect fun createHttpClient(authSessionManager: AuthSessionManager): HttpClient

internal fun appJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
}
