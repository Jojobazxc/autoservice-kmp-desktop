package com.example.autoservice_desktop.features.parts.presentation

internal sealed interface PartsAction {
    data object Load : PartsAction

    data object OpenCreateDialog : PartsAction
    data object CloseCreateDialog : PartsAction
    data class ChangeCreateName(val value: String) : PartsAction
    data class ChangeCreateArticle(val value: String) : PartsAction
    data class ChangeCreatePrice(val value: String) : PartsAction
    data class ChangeCreateUnit(val value: String) : PartsAction
    data class ChangeCreateStockQuantity(val value: String) : PartsAction
    data object SubmitCreate : PartsAction
}