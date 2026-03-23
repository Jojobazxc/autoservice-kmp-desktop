package com.example.autoservice_desktop.features.clients.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autoservice_desktop.core.ui.AppTableToolbar
import com.example.autoservice_desktop.core.ui.formatClientStatus
import com.example.autoservice_desktop.core.ui.theme.AppColors
import com.example.autoservice_desktop.features.clients.data.ClientDto
import com.example.autoservice_desktop.features.clients.presentation.ClientsAction
import com.example.autoservice_desktop.features.clients.presentation.ClientsStore

@Composable
internal fun ClientsScreen(
    store: ClientsStore
) {
    val state by store.state.collectAsState()

    LaunchedEffect(Unit) {
        store.dispatch(ClientsAction.Load)
    }

    val filteredItems = remember(state.items) { state.items }

    if (state.isCreateDialogOpen) {
        CreateClientDialog(
            state = state,
            onAction = store::dispatch
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Клиенты",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        AppTableToolbar(
            searchPlaceholder = "Поиск появится позже",
            onAdd = { store.dispatch(ClientsAction.OpenCreateDialog) },
            onRefresh = { store.dispatch(ClientsAction.Load) }
        )

        when {
            state.isLoading -> {
                Text("Загрузка...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            state.error != null -> {
                Text(
                    text = "Ошибка: ${state.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                ClientsTable(items = filteredItems)
            }
        }
    }
}

@Composable
private fun ClientsTable(
    items: List<ClientDto>
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ClientsTableHeader()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items) { index, client ->
                    ClientTableRow(
                        index = index + 1,
                        client = client
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientsTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.TableHeaderBackground)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("ID", 0.7f)
        HeaderCell("ФИО", 2.2f)
        HeaderCell("Телефон", 1.7f)
        HeaderCell("Email", 2.0f)
        HeaderCell("Адрес", 2.0f)
        HeaderCell("Статус", 1.2f)
    }
}

@Composable
private fun ClientTableRow(
    index: Int,
    client: ClientDto
) {
    val defaultBackground = if (index % 2 == 0) {
        AppColors.TableRowEven
    } else {
        AppColors.TableRowOdd
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val background = if (isHovered) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
    } else {
        defaultBackground
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Cell(client.id.toString(), 0.7f)
        Cell(client.fullName, 2.2f)
        Cell(client.phone, 1.7f)
        Cell(client.email ?: "-", 2.0f)
        Cell(client.address ?: "-", 2.0f)
        StatusCell(formatClientStatus(client.status), 1.2f)
    }
}

@Composable
private fun RowScope.HeaderCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier
            .weight(weight)
            .padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.Cell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .weight(weight)
            .padding(end = 8.dp)
    )
}

@Composable
private fun RowScope.StatusCell(
    status: String,
    weight: Float
) {
    Row(
        modifier = Modifier
            .weight(weight)
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}