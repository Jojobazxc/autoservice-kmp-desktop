package com.example.autoservice_desktop.features.parts.data

import kotlinx.serialization.Serializable

@Serializable
internal data class CreatePartRequest(
    val name: String,
    val article: String,
    val price: String,
    val unit: String = "pcs",
    val stockQuantity: Int = 0
)