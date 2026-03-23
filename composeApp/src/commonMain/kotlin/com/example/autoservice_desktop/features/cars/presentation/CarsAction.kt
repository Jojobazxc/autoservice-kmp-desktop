package com.example.autoservice_desktop.features.cars.presentation

internal sealed interface CarsAction {
    data object Load : CarsAction

    data object OpenCreateDialog : CarsAction
    data object CloseCreateDialog : CarsAction
    data class ChangeCreateClient(val clientId: Long?) : CarsAction
    data class ChangeCreateBrand(val value: String) : CarsAction
    data class ChangeCreateModel(val value: String) : CarsAction
    data class ChangeCreateYear(val value: String) : CarsAction
    data class ChangeCreatePlateNumber(val value: String) : CarsAction
    data class ChangeCreateVin(val value: String) : CarsAction
    data class ChangeCreateMileage(val value: String) : CarsAction
    data object SubmitCreate : CarsAction
}