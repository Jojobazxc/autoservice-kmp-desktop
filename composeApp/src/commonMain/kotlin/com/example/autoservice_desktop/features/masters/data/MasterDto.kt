package com.example.autoservice_desktop.features.masters.data

import kotlinx.serialization.Serializable

@Serializable
internal data class MasterDto(
    val id: Long,
    val fullName: String,
    val specialization: String? = null,
    val experienceYears: Int? = null,
    val phone: String? = null,
    val email: String? = null,
    val employmentStatus: String
)