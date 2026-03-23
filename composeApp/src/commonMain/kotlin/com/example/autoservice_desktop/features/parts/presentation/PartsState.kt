package com.example.autoservice_desktop.features.parts.presentation

import com.example.autoservice_desktop.features.parts.data.PartDto

internal data class PartsState(
    val items: List<PartDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    val isCreateDialogOpen: Boolean = false,
    val createForm: CreatePartForm = CreatePartForm()
)

internal data class CreatePartForm(
    val name: String = "",
    val article: String = "",
    val price: String = "",
    val unit: String = "pcs",
    val stockQuantity: String = "0",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)