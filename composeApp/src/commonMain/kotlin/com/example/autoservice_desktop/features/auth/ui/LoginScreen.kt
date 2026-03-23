package com.example.autoservice_desktop.features.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
)