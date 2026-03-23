package com.example.autoservice_desktop.features.parts.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class PartsApi(
    private val httpClient: HttpClient
) {
    suspend fun getParts(): List<PartDto> {
        return httpClient
            .get("${ApiConfig.BASE_URL}/parts")
            .body()
    }

    suspend fun createPart(request: CreatePartRequest): PartDto {
        return httpClient
            .post("${ApiConfig.BASE_URL}/parts") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()
    }
}