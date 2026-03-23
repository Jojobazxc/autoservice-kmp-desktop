package com.example.autoservice_desktop.features.cars.presentation

import com.example.autoservice_desktop.features.cars.data.CarsRepository
import com.example.autoservice_desktop.features.cars.data.CreateCarRequest
import com.example.autoservice_desktop.features.clients.data.ClientDto
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

    private var cachedClients: List<ClientDto> = emptyList()

    internal fun dispatch(action: CarsAction) {
        when (action) {
            CarsAction.Load -> loadCars()

            CarsAction.OpenCreateDialog -> openCreateDialog()
            CarsAction.CloseCreateDialog -> closeCreateDialog()
            is CarsAction.ChangeCreateClient -> changeCreateClient(action.clientId)
            is CarsAction.ChangeCreateBrand -> changeCreateBrand(action.value)
            is CarsAction.ChangeCreateModel -> changeCreateModel(action.value)
            is CarsAction.ChangeCreateYear -> changeCreateYear(action.value)
            is CarsAction.ChangeCreatePlateNumber -> changeCreatePlateNumber(action.value)
            is CarsAction.ChangeCreateVin -> changeCreateVin(action.value)
            is CarsAction.ChangeCreateMileage -> changeCreateMileage(action.value)
            CarsAction.SubmitCreate -> submitCreate()
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
                _state.value = _state.value.copy(
                    items = items,
                    isLoading = false,
                    error = null
                )
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    items = emptyList(),
                    isLoading = false,
                    error = throwable.message ?: "Неизвестная ошибка"
                )
            }
        }
    }

    private fun openCreateDialog() {
        scope.launch {
            runCatching {
                cachedClients = clientsRepository.getClients()
            }.onSuccess {
                _state.value = _state.value.copy(
                    isCreateDialogOpen = true,
                    createForm = CreateCarForm(),
                    clientOptions = cachedClients.map {
                        CarReferenceOptionUi(it.id, "#${it.id} ${it.fullName}")
                    }
                )
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isCreateDialogOpen = true,
                    createForm = CreateCarForm(
                        errorMessage = throwable.message ?: "Не удалось загрузить клиентов"
                    ),
                    clientOptions = emptyList()
                )
            }
        }
    }

    private fun closeCreateDialog() {
        _state.value = _state.value.copy(
            isCreateDialogOpen = false,
            createForm = CreateCarForm()
        )
    }

    private fun changeCreateClient(clientId: Long?) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(clientId = clientId, errorMessage = null)
        )
    }

    private fun changeCreateBrand(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(brand = value, errorMessage = null)
        )
    }

    private fun changeCreateModel(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(model = value, errorMessage = null)
        )
    }

    private fun changeCreateYear(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(year = value, errorMessage = null)
        )
    }

    private fun changeCreatePlateNumber(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(plateNumber = value, errorMessage = null)
        )
    }

    private fun changeCreateVin(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(vin = value, errorMessage = null)
        )
    }

    private fun changeCreateMileage(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(mileage = value, errorMessage = null)
        )
    }

    private fun submitCreate() {
        val form = _state.value.createForm

        when {
            form.clientId == null -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Выбери клиента")
                )
                return
            }

            form.brand.isBlank() -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Введи марку")
                )
                return
            }

            form.model.isBlank() -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Введи модель")
                )
                return
            }

            form.plateNumber.isBlank() -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Введи гос. номер")
                )
                return
            }

            form.year.isNotBlank() && form.year.toIntOrNull() == null -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Год должен быть числом")
                )
                return
            }

            form.mileage.isNotBlank() && form.mileage.toIntOrNull() == null -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Пробег должен быть числом")
                )
                return
            }
        }

        scope.launch {
            _state.value = _state.value.copy(
                createForm = form.copy(isSubmitting = true, errorMessage = null)
            )

            runCatching {
                carsRepository.createCar(
                    CreateCarRequest(
                        clientId = form.clientId,
                        brand = form.brand.trim(),
                        model = form.model.trim(),
                        year = form.year.toIntOrNull(),
                        plateNumber = form.plateNumber.trim(),
                        vin = form.vin.trim().ifBlank { null },
                        mileage = form.mileage.toIntOrNull()
                    )
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    isCreateDialogOpen = false,
                    createForm = CreateCarForm()
                )
                loadCars()
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    createForm = _state.value.createForm.copy(
                        isSubmitting = false,
                        errorMessage = throwable.message ?: "Не удалось добавить автомобиль"
                    )
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