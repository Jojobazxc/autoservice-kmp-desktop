package com.example.autoservice_desktop.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppLightColors = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryContainer,
    onPrimaryContainer = AppColors.OnPrimaryContainer,

    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnSecondary,

    background = AppColors.Background,
    onBackground = AppColors.OnBackground,

    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,

    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.OnSurfaceVariant,

    outline = AppColors.Outline,
    outlineVariant = AppColors.OutlineVariant,

    error = AppColors.Error,
    onError = AppColors.OnError
)

@Composable
fun AutoServiceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppLightColors,
        typography = AppTypography,
        content = content
    )
}