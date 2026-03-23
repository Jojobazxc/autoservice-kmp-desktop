package com.example.autoservice_desktop.features.cars.presentation

import com.example.autoservice_desktop.features.cars.data.CarsRepository
import com.example.autoservice_desktop.features.clients.data.ClientsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class CarsStore(
    private val carsRepository: CarsRepository,
    private val clientsRepository: ClientsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(CarsState())
    internal val state: StateFlow<CarsState> = _state.asStateFlow()

    internal fun dispatch(action: CarsAction) {
        when (action) {
            CarsAction.Load -> loadCars()
        }
    }

    private fun loadCars() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching {
                val cars = carsRepository.getCars()
                val clients = clientsRepository.getClients()

                val clientsById = clients.associateBy { it.id }

                cars.map { car ->
                    val fullName = clientsById[car.clientId]?.fullName ?: "Неизвестный клиент"

                    CarListItem(
                        id = car.id,
                        ownerName = "#${car.clientId} ${toShortName(fullName)}",
                        brand = car.brand,
                        model = car.model,
                        year = car.year,
                        plateNumber = car.plateNumber,
                        vin = car.vin,
                        mileage = car.mileage
                    )
                }
            }.onSuccess { items ->
                _state.value = CarsState(
                    items = items,
                    isLoading = false,
                    error = null
                )
            }.onFailure { throwable ->
                _state.value = CarsState(
                    items = emptyList(),
                    isLoading = false,
                    error = throwable.message ?: "Неизвестная ошибка"
                )
            }
        }
    }

    private fun toShortName(fullName: String): String {
        val parts = fullName.trim().split("\\s+".toRegex())
        if (parts.isEmpty()) return fullName

        return when (parts.size) {
            1 -> parts[0]
            2 -> "${parts[0]} ${parts[1].first()}."
            else -> "${parts[0]} ${parts[1].first()}.${parts[2].first()}."
        }
    }
}