package com.example.autoservice_desktop.features.services.data

internal class ServicesRepository(
    private val api: ServicesApi
) {
    suspend fun getServices(): List<ServiceDto> = api.getServices()

    suspend fun createService(request: CreateServiceRequest): ServiceDto = api.createService(request)
}