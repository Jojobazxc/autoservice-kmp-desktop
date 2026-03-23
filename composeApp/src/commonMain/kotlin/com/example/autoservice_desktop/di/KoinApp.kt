package com.example.autoservice_desktop.di

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication

@Composable
internal fun AppKoin(
    content: @Composable () -> Unit
) {
    KoinApplication(
        application = {
            modules(appModule)
        }
    ) {
        content()
    }
}