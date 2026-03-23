package com.example.autoservice_desktop.navigation

internal sealed class Screen(
    val title: String,
    val icon: String
) {
    data object Clients : Screen("Клиенты", "👥")
    data object Cars : Screen("Автомобили", "🚗")
    data object Masters : Screen("Мастера", "🧰")
    data object Services : Screen("Услуги", "🛠")
    data object Parts : Screen("Запчасти", "📦")
    data object Orders : Screen("Заказы", "📋")
}