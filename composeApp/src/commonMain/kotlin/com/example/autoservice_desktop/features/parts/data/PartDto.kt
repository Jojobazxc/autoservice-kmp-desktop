package com.example.autoservice_desktop.features.parts.data

import kotlinx.serialization.Serializable

@Serializable
internal data class PartDto(
    val id: Long,
    val name: String,
    val article: String,
    val price: String,
    val unit: String,
    val stockQuantity: Int
)