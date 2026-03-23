package com.example.autoservice_desktop.features.masters.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class MastersApi(
    private val httpClient: HttpClient
) {
    suspend fun getMasters(): List<MasterDto> {
        return httpClient
            .get("${ApiConfig.BASE_URL}/masters")
            .body<List<MasterDto>>()
    }

    suspend fun createMaster(request: CreateMasterRequest): MasterDto {
        return httpClient
            .post("${ApiConfig.BASE_URL}/masters") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body<MasterDto>()
    }
}