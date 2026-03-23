package com.example.autoservice_desktop.features.parts.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class PartsApi(
    private val httpClient: HttpClient
) {
    suspend fun getParts(): List<PartDto> {
        return httpClient
            .get("${ApiConfig.BASE_URL}/parts")
            .body<List<PartDto>>()
    }
}