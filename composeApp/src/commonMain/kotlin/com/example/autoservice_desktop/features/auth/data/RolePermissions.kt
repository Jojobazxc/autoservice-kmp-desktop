package com.example.autoservice_desktop.features.auth.data

internal fun UserRole.canManageOrderServices(): Boolean {
    return this == UserRole.ADMIN || this == UserRole.MANAGER || this == UserRole.MECHANIC
}

internal fun UserRole.canManageOrderParts(): Boolean {
    return this == UserRole.ADMIN || this == UserRole.MANAGER || this == UserRole.MECHANIC
}

internal fun UserRole.canManageOrderPayments(): Boolean {
    return this == UserRole.ADMIN || this == UserRole.ACCOUNTANT
}

internal fun UserRole.canEditOrders(): Boolean {
    return this == UserRole.ADMIN || this == UserRole.MANAGER
}

internal fun UserRole.canCompleteOrders(): Boolean {
    return this == UserRole.ADMIN || this == UserRole.MANAGER || this == UserRole.MECHANIC
}

internal fun UserRole.canCancelOrders(): Boolean {
    return this == UserRole.ADMIN || this == UserRole.MANAGER
}
