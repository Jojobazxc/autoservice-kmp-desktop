package com.example.autoservice_desktop.features.masters.data

internal class MastersRepository(
    private val api: MastersApi
) {
    suspend fun getMasters(): List<MasterDto> = api.getMasters()

    suspend fun createMaster(request: CreateMasterRequest): MasterDto = api.createMaster(request)
}