package com.example.autoservice_desktop.features.parts.data

internal class PartsRepository(
    private val api: PartsApi
) {
    suspend fun getParts(): List<PartDto> = api.getParts()

    suspend fun createPart(request: CreatePartRequest): PartDto = api.createPart(request)
}