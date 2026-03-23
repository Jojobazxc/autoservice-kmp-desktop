package com.example.autoservice_desktop.features.clients.data

import kotlinx.serialization.Serializable

@Serializable
internal data class ClientDto(
    val id: Long,
    val fullName: String,
    val phone: String,
    val email: String? = null,
    val address: String? = null,
    val registrationDate: String,
    val status: String
)