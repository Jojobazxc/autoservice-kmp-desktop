package com.example.autoservice_desktop.features.services.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class ServicesApi(
    private val httpClient: HttpClient
) {
    suspend fun getServices(): List<ServiceDto> {
        return httpClient
            .get("${ApiConfig.BASE_URL}/services")
            .body()
    }

    suspend fun createService(request: CreateServiceRequest): ServiceDto {
        return httpClient
            .post("${ApiConfig.BASE_URL}/services") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()
    }
}