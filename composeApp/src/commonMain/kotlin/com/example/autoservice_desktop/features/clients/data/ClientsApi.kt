package com.example.autoservice_desktop.features.clients.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class ClientsApi(
    private val httpClient: HttpClient
) {
    suspend fun getClients(): List<ClientDto> {
        return httpClient
            .get("${ApiConfig.BASE_URL}/clients")
            .body<List<ClientDto>>()
    }

    suspend fun createClient(request: CreateClientRequest): ClientDto {
        return httpClient
            .post("${ApiConfig.BASE_URL}/clients") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body<ClientDto>()
    }
}