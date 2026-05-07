package com.example.autoservice_desktop.core.network

import com.example.autoservice_desktop.features.auth.data.AuthSessionManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json

internal actual fun createHttpClient(authSessionManager: AuthSessionManager): HttpClient {
    return HttpClient(CIO) {
        install(ContentNegotiation) {
            json(appJson())
        }
        defaultRequest {
            val session = authSessionManager.currentSession()
            if (session != null) {
                header(HttpHeaders.Authorization, "${session.tokenType} ${session.token}")
            }
        }
        HttpResponseValidator {
            validateResponse { response ->
                when (response.status) {
                    HttpStatusCode.Unauthorized -> {
                        authSessionManager.clear()
                        throw UnauthorizedApiException()
                    }

                    HttpStatusCode.Forbidden -> throw ForbiddenApiException()
                }
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}
