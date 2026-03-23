package com.example.autoservice_desktop.features.cars.presentation

internal sealed interface CarsAction {
    data object Load : CarsAction
}