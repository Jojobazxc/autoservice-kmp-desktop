package com.example.autoservice_desktop.features.auth.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class AuthApi(
    private val httpClient: HttpClient
) {
    suspend fun login(request: LoginRequest): LoginResponse {
        return httpClient
            .post("${ApiConfig.BASE_URL}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()
    }
}
