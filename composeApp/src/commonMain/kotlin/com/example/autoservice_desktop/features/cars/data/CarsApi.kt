package com.example.autoservice_desktop.features.cars.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class CarsApi(
    private val httpClient: HttpClient
) {
    suspend fun getCars(): List<CarDto> {
        return httpClient
            .get("${ApiConfig.BASE_URL}/cars")
            .body()
    }

    suspend fun createCar(request: CreateCarRequest): CarDto {
        return httpClient
            .post("${ApiConfig.BASE_URL}/cars") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()
    }
}