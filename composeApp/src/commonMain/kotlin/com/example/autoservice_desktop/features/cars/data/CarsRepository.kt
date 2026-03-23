package com.example.autoservice_desktop.features.cars.data

internal class CarsRepository(
    private val api: CarsApi
) {
    suspend fun getCars(): List<CarDto> = api.getCars()

    suspend fun createCar(request: CreateCarRequest): CarDto = api.createCar(request)
}