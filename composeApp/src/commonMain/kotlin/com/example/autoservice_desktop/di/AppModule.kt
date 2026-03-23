package com.example.autoservice_desktop.di

import com.example.autoservice_desktop.core.network.createHttpClient
import com.example.autoservice_desktop.features.cars.data.CarsApi
import com.example.autoservice_desktop.features.cars.data.CarsRepository
import com.example.autoservice_desktop.features.cars.presentation.CarsStore
import com.example.autoservice_desktop.features.clients.data.ClientsApi
import com.example.autoservice_desktop.features.clients.data.ClientsRepository
import com.example.autoservice_desktop.features.clients.presentation.ClientsStore
import com.example.autoservice_desktop.features.masters.data.MastersApi
import com.example.autoservice_desktop.features.masters.data.MastersRepository
import com.example.autoservice_desktop.features.masters.presentation.MastersStore
import com.example.autoservice_desktop.features.orders.data.OrdersApi
import com.example.autoservice_desktop.features.orders.data.OrdersRepository
import com.example.autoservice_desktop.features.orders.presentation.OrdersStore
import com.example.autoservice_desktop.features.parts.data.PartsApi
import com.example.autoservice_desktop.features.parts.data.PartsRepository
import com.example.autoservice_desktop.features.parts.presentation.PartsStore
import com.example.autoservice_desktop.features.services.data.ServicesApi
import com.example.autoservice_desktop.features.services.data.ServicesRepository
import com.example.autoservice_desktop.features.services.presentation.ServicesStore
import com.example.autoservice_desktop.navigation.AppRouter
import org.koin.dsl.module

internal val appModule = module {
    single { createHttpClient() }

    single { AppRouter() }

    single { ClientsApi(get()) }
    single { ClientsRepository(get()) }
    factory { ClientsStore(get()) }

    single { CarsApi(get()) }
    single { CarsRepository(get()) }
    factory { CarsStore(get(), get()) }

    single { MastersApi(get()) }
    single { MastersRepository(get()) }
    factory { MastersStore(get()) }

    single { ServicesApi(get()) }
    single { ServicesRepository(get()) }
    factory { ServicesStore(get()) }

    single { PartsApi(get()) }
    single { PartsRepository(get()) }
    factory { PartsStore(get()) }

    single { OrdersApi(get()) }
    single { OrdersRepository(get()) }
    factory { OrdersStore(get(), get(), get(), get(), get(), get()) }


}