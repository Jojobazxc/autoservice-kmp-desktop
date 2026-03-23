package com.example.autoservice_desktop.features.clients.data

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateClientRequest(
    val fullName: String,
    val phone: String,
    val email: String? = null,
    val address: String? = null,
    val status: String = "REGULAR"
)