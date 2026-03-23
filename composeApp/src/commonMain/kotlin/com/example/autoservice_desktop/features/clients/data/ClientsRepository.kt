package com.example.autoservice_desktop.features.clients.data

internal class ClientsRepository(
    private val api: ClientsApi
) {
    suspend fun getClients(): List<ClientDto> = api.getClients()

    suspend fun createClient(request: CreateClientRequest): ClientDto = api.createClient(request)
}