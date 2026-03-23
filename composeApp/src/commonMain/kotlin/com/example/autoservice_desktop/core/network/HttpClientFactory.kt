package com.example.autoservice_desktop.core.network

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

internal expect fun createHttpClient(): HttpClient

internal fun appJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
}