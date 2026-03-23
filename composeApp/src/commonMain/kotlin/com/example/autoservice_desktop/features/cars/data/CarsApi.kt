package com.example.autoservice_desktop.features.cars.data

import com.example.autoservice_desktop.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class CarsApi(
    private val httpClient: HttpClient
) {
    suspend fun getCars(): List<CarDto> {
        return httpClient
            .get("${ApiConfig.BASE_URL}/cars")
            .body<List<CarDto>>()
    }
}