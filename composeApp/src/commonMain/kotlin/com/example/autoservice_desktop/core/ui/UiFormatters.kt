package com.example.autoservice_desktop.core.ui

internal fun formatClientStatus(status: String): String {
    return when (status) {
        "REGULAR" -> "Обычный"
        "VIP" -> "VIP"
        else -> status
    }
}

internal fun formatEmploymentStatus(status: String): String {
    return when (status) {
        "ACTIVE" -> "Активен"
        "INACTIVE" -> "Неактивен"
        else -> status
    }
}

internal fun formatOrderStatus(status: String): String {
    return when (status) {
        "CREATED" -> "Создан"
        "IN_PROGRESS" -> "В работе"
        "COMPLETED" -> "Завершён"
        "PAID" -> "Оплачен"
        "CANCELED" -> "Отменён"
        else -> status
    }
}

internal fun formatPaymentStatus(status: String): String {
    return when (status) {
        "PENDING" -> "Ожидает"
        "PAID" -> "Оплачено"
        "FAILED" -> "Ошибка"
        else -> status
    }
}

internal fun formatPaymentMethod(method: String): String {
    return when (method) {
        "CASH" -> "Наличные"
        "CARD" -> "Карта"
        else -> method
    }
}

internal fun formatPartUnit(unit: String): String {
    return when (unit.lowercase()) {
        "pcs" -> "шт."
        "piece" -> "шт."
        "pieces" -> "шт."
        "l" -> "л"
        "liter" -> "л"
        "liters" -> "л"
        "kg" -> "кг"
        "g" -> "г"
        "set" -> "компл."
        else -> unit
    }
}