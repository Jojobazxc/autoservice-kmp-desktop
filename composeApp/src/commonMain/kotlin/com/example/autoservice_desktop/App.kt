package com.example.autoservice_desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autoservice_desktop.core.ui.NavigationItem
import com.example.autoservice_desktop.core.ui.theme.AppColors
import com.example.autoservice_desktop.core.ui.theme.AutoServiceTheme
import com.example.autoservice_desktop.di.AppKoin
import com.example.autoservice_desktop.features.auth.ui.LoginScreen
import com.example.autoservice_desktop.features.cars.presentation.CarsStore
import com.example.autoservice_desktop.features.cars.ui.CarsScreen
import com.example.autoservice_desktop.features.clients.presentation.ClientsStore
import com.example.autoservice_desktop.features.clients.ui.ClientsScreen
import com.example.autoservice_desktop.features.masters.presentation.MastersStore
import com.example.autoservice_desktop.features.masters.ui.MastersScreen
import com.example.autoservice_desktop.features.orders.presentation.OrdersStore
import com.example.autoservice_desktop.features.orders.ui.OrdersScreen
import com.example.autoservice_desktop.features.parts.presentation.PartsStore
import com.example.autoservice_desktop.features.parts.ui.PartsScreen
import com.example.autoservice_desktop.features.services.presentation.ServicesStore
import com.example.autoservice_desktop.features.services.ui.ServicesScreen
import com.example.autoservice_desktop.navigation.AppRouter
import com.example.autoservice_desktop.navigation.Screen
import org.koin.compose.koinInject

@Composable
fun App() {
    AppKoin {
        AutoServiceTheme {
            var isAuthorized by rememberSaveable { mutableStateOf(false) }

            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                if (isAuthorized) {
                    MainCrmShell()
                } else {
                    LoginScreen(
                        onLoginSuccess = { isAuthorized = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainCrmShell() {
    val router: AppRouter = koinInject()
    val clientsStore: ClientsStore = koinInject()
    val carsStore: CarsStore = koinInject()
    val mastersStore: MastersStore = koinInject()
    val servicesStore: ServicesStore = koinInject()
    val partsStore: PartsStore = koinInject()
    val ordersStore: OrdersStore = koinInject()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(currentScreenTitle = router.currentScreen.title)

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Sidebar(
                currentScreen = router.currentScreen,
                onScreenSelected = router::navigateTo
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp)
            ) {
                when (router.currentScreen) {
                    Screen.Clients -> ClientsScreen(clientsStore)
                    Screen.Cars -> CarsScreen(carsStore)
                    Screen.Masters -> MastersScreen(mastersStore)
                    Screen.Services -> ServicesScreen(servicesStore)
                    Screen.Parts -> PartsScreen(partsStore)
                    Screen.Orders -> OrdersScreen(ordersStore)
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    currentScreenTitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Auto Service CRM",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currentScreenTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🟢 Backend online",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.Success
                )

                Text(
                    text = "Desktop MVP",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun Sidebar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    val items = listOf(
        Screen.Clients,
        Screen.Cars,
        Screen.Masters,
        Screen.Services,
        Screen.Parts,
        Screen.Orders
    )

    Column(
        modifier = Modifier
            .width(250.dp)
            .fillMaxHeight()
            .background(AppColors.SidebarBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Разделы",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )

        items.forEach { screen ->
            NavigationItem(
                icon = screen.icon,
                title = screen.title,
                selected = currentScreen == screen,
                onClick = { onScreenSelected(screen) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Система",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Backend: online",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.Success
            )
        }
    }
}