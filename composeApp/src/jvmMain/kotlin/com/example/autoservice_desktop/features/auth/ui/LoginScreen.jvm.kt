package com.example.autoservice_desktop.features.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import autoservice_desktop.composeapp.generated.resources.Res
import autoservice_desktop.composeapp.generated.resources.login_background
import com.example.autoservice_desktop.features.auth.data.AuthSessionManager
import com.example.autoservice_desktop.features.auth.presentation.AuthAction
import com.example.autoservice_desktop.features.auth.presentation.AuthStore
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
internal actual fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier
) {
    val store: AuthStore = koinInject()
    val sessionManager: AuthSessionManager = koinInject()
    val state by store.state.collectAsState()
    val session by sessionManager.session.collectAsState()

    LaunchedEffect(session) {
        if (session != null) {
            onLoginSuccess()
        }
    }

    Row(
        modifier = modifier.fillMaxSize()
    ) {
        LoginLeftPanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp)
                    .padding(32.dp),
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 36.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Auto Service CRM",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Вход в систему",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedTextField(
                        value = state.login,
                        onValueChange = { store.dispatch(AuthAction.ChangeLogin(it)) },
                        label = { Text("Логин") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !state.isLoading
                    )

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { store.dispatch(AuthAction.ChangePassword(it)) },
                        label = { Text("Пароль") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !state.isLoading
                    )

                    state.error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            store.dispatch(AuthAction.SubmitLogin)
                        },
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 10.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        }
                        Text(if (state.isLoading) "Вход..." else "Войти")
                    }

                    TextButton(
                        onClick = {},
                        enabled = !state.isLoading,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Забыли пароль?")
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginLeftPanel(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(Res.drawable.login_background),
            contentDescription = "Login background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
