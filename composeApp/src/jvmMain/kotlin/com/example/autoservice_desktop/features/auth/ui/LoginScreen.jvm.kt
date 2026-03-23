package com.example.autoservice_desktop.features.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import autoservice_desktop.composeapp.generated.resources.Res
import autoservice_desktop.composeapp.generated.resources.login_background
import org.jetbrains.compose.resources.painterResource

@Composable
internal actual fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier
) {
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }

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
                        value = login,
                        onValueChange = {
                            login = it
                            errorText = null
                        },
                        label = { Text("Логин или email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorText = null
                        },
                        label = { Text("Пароль") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    errorText?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            if (login.isBlank() || password.isBlank()) {
                                errorText = "Заполните логин и пароль"
                            } else {
                                onLoginSuccess()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Войти")
                    }

                    TextButton(
                        onClick = {},
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