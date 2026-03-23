package com.example.autoservice_desktop.features.services.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class ServicesApi(
    private val httpClient: HttpClient
) {
    suspend fun getServices(): List<ServiceDto> {
        return httpClient
            .get("${ApiConfig.BASE_URL}/services")
            .body<List<ServiceDto>>()
    }
}