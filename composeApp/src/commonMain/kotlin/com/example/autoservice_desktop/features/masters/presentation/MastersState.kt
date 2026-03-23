package com.example.autoservice_desktop.features.masters.presentation

import com.example.autoservice_desktop.features.masters.data.MasterDto

internal data class MastersState(
    val items: List<MasterDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    val isCreateDialogOpen: Boolean = false,
    val isCreating: Boolean = false,

    val fullNameInput: String = "",
    val specializationInput: String = "",
    val experienceYearsInput: String = "",
    val phoneInput: String = "",
    val emailInput: String = "",
    val employmentStatusInput: String = "ACTIVE",

    val createError: String? = null
)