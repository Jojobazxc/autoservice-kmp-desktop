package com.example.autoservice_desktop.navigation

import com.example.autoservice_desktop.features.auth.data.UserRole

internal sealed class Screen(
    val title: String,
    val icon: String,
    private val allowedRoles: Set<UserRole>
) {
    data object Clients : Screen("Клиенты", "👥", setOf(UserRole.ADMIN, UserRole.MANAGER))
    data object Cars : Screen("Автомобили", "🚗", setOf(UserRole.ADMIN, UserRole.MANAGER))
    data object Masters : Screen("Мастера", "🧰", setOf(UserRole.ADMIN, UserRole.MANAGER))
    data object Services : Screen("Услуги", "🛠", setOf(UserRole.ADMIN, UserRole.MANAGER, UserRole.MECHANIC))
    data object Parts : Screen("Запчасти", "📦", setOf(UserRole.ADMIN, UserRole.MANAGER, UserRole.MECHANIC))
    data object Orders : Screen("Заказы", "📋", setOf(UserRole.ADMIN, UserRole.MANAGER, UserRole.MECHANIC))
    data object Payments : Screen("Оплаты", "₽", setOf(UserRole.ADMIN, UserRole.ACCOUNTANT))
    data object Reports : Screen("Отчеты", "📊", setOf(UserRole.ADMIN, UserRole.ACCOUNTANT))

    fun isAllowedFor(role: UserRole): Boolean = role in allowedRoles

    companion object {
        fun availableFor(role: UserRole): List<Screen> {
            return listOf(
                Clients,
                Cars,
                Masters,
                Services,
                Parts,
                Orders,
                Payments,
                Reports
            ).filter { it.isAllowedFor(role) }
        }
    }
}
