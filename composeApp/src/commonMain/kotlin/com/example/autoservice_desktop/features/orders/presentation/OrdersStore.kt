package com.example.autoservice_desktop.features.orders.presentation

import com.example.autoservice_desktop.core.ui.formatOrderStatus
import com.example.autoservice_desktop.core.ui.formatPaymentMethod
import com.example.autoservice_desktop.core.ui.formatPaymentStatus
import com.example.autoservice_desktop.features.cars.data.CarDto
import com.example.autoservice_desktop.features.cars.data.CarsRepository
import com.example.autoservice_desktop.features.clients.data.ClientDto
import com.example.autoservice_desktop.features.clients.data.ClientsRepository
import com.example.autoservice_desktop.features.masters.data.MasterDto
import com.example.autoservice_desktop.features.masters.data.MastersRepository
import com.example.autoservice_desktop.features.orders.data.AddOrderPartRequest
import com.example.autoservice_desktop.features.orders.data.AddOrderPaymentRequest
import com.example.autoservice_desktop.features.orders.data.AddOrderServiceRequest
import com.example.autoservice_desktop.features.orders.data.CreateOrderRequest
import com.example.autoservice_desktop.features.orders.data.OrderDetailsDto
import com.example.autoservice_desktop.features.orders.data.OrdersRepository
import com.example.autoservice_desktop.features.parts.data.PartDto
import com.example.autoservice_desktop.features.parts.data.PartsRepository
import com.example.autoservice_desktop.features.services.data.ServiceDto
import com.example.autoservice_desktop.features.services.data.ServicesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

internal class OrdersStore(
    private val ordersRepository: OrdersRepository,
    private val clientsRepository: ClientsRepository,
    private val carsRepository: CarsRepository,
    private val mastersRepository: MastersRepository,
    private val servicesRepository: ServicesRepository,
    private val partsRepository: PartsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(OrdersState())
    internal val state: StateFlow<OrdersState> = _state.asStateFlow()

    private var cachedClients: List<ClientDto> = emptyList()
    private var cachedCars: List<CarDto> = emptyList()
    private var cachedMasters: List<MasterDto> = emptyList()
    private var cachedServices: List<ServiceDto> = emptyList()
    private var cachedParts: List<PartDto> = emptyList()

    fun dispatch(action: OrdersAction) {
        when (action) {
            OrdersAction.Load -> loadOrders()
            is OrdersAction.SelectOrder -> selectOrder(action.orderId)

            OrdersAction.OpenCreateDialog -> openCreateDialog()
            OrdersAction.CloseCreateDialog -> closeCreateDialog()
            is OrdersAction.ChangeCreateClient -> changeCreateClient(action.clientId)
            is OrdersAction.ChangeCreateCar -> changeCreateCar(action.carId)
            is OrdersAction.ChangeCreateMaster -> changeCreateMaster(action.masterId)
            is OrdersAction.ChangeCreateDescription -> changeCreateDescription(action.value)
            is OrdersAction.ChangeCreateComment -> changeCreateComment(action.value)
            is OrdersAction.ChangeCreateStatus -> changeCreateStatus(action.value)
            is OrdersAction.ChangeCreatePlannedCompletionAt -> changeCreatePlannedCompletionAt(action.value)
            OrdersAction.SubmitCreate -> submitCreate()

            OrdersAction.OpenAddServiceDialog -> openAddServiceDialog()
            OrdersAction.CloseAddServiceDialog -> closeAddServiceDialog()
            is OrdersAction.ChangeAddServiceSelected -> changeAddServiceSelected(action.serviceId)
            is OrdersAction.ChangeAddServiceQuantity -> changeAddServiceQuantity(action.value)
            is OrdersAction.ChangeAddServicePrice -> changeAddServicePrice(action.value)
            OrdersAction.SubmitAddService -> submitAddService()

            OrdersAction.OpenAddPartDialog -> openAddPartDialog()
            OrdersAction.CloseAddPartDialog -> closeAddPartDialog()
            is OrdersAction.ChangeAddPartSelected -> changeAddPartSelected(action.partId)
            is OrdersAction.ChangeAddPartQuantity -> changeAddPartQuantity(action.value)
            is OrdersAction.ChangeAddPartPrice -> changeAddPartPrice(action.value)
            OrdersAction.SubmitAddPart -> submitAddPart()

            OrdersAction.OpenAddPaymentDialog -> openAddPaymentDialog()
            OrdersAction.CloseAddPaymentDialog -> closeAddPaymentDialog()
            is OrdersAction.ChangeAddPaymentAmount -> changeAddPaymentAmount(action.value)
            is OrdersAction.ChangeAddPaymentMethod -> changeAddPaymentMethod(action.value)
            is OrdersAction.ChangeAddPaymentStatus -> changeAddPaymentStatus(action.value)
            OrdersAction.SubmitAddPayment -> submitAddPayment()
        }
    }

    private fun loadOrders(selectOrderId: Long? = null) {
        scope.launch {
            _state.value = _state.value.copy(
                isLoadingList = true,
                listError = null
            )

            runCatching {
                val orders = ordersRepository.getOrders()
                val clients = clientsRepository.getClients().associateBy { it.id }
                val cars = carsRepository.getCars().associateBy { it.id }
                val masters = mastersRepository.getMasters().associateBy { it.id }

                orders.map { order ->
                    val clientName = clients[order.clientId]?.fullName ?: "Неизвестный клиент"
                    val car = cars[order.carId]
                    val masterName = order.masterId?.let { masters[it]?.fullName }

                    OrderListItemUi(
                        id = order.id,
                        clientDisplay = "#${order.clientId} ${toShortName(clientName)}",
                        carDisplay = if (car != null) {
                            "#${car.id} ${car.brand} ${car.model}"
                        } else {
                            "#${order.carId} Неизвестное авто"
                        },
                        masterDisplay = if (order.masterId != null) {
                            "#${order.masterId} ${masterName?.let(::toShortName) ?: "Неизвестный мастер"}"
                        } else {
                            "-"
                        },
                        status = formatOrderStatus(order.status),
                        createdAt = formatDateTime(order.createdAt),
                        totalAmount = order.totalAmount
                    )
                }
            }.onSuccess { items ->
                val targetOrderId = when {
                    items.isEmpty() -> null
                    selectOrderId != null && items.any { it.id == selectOrderId } -> selectOrderId
                    else -> items.first().id
                }

                _state.value = _state.value.copy(
                    items = items,
                    isLoadingList = false,
                    listError = null,
                    selectedOrderId = targetOrderId
                )

                if (targetOrderId != null) {
                    loadOrderDetails(targetOrderId)
                } else {
                    _state.value = _state.value.copy(
                        selectedOrderDetails = null,
                        isLoadingDetails = false,
                        detailsError = null
                    )
                }
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    items = emptyList(),
                    isLoadingList = false,
                    listError = throwable.message ?: "Не удалось загрузить заказы"
                )
            }
        }
    }

    private fun selectOrder(orderId: Long) {
        if (_state.value.selectedOrderId == orderId) return

        _state.value = _state.value.copy(selectedOrderId = orderId)
        loadOrderDetails(orderId)
    }

    private fun loadOrderDetails(orderId: Long) {
        scope.launch {
            _state.value = _state.value.copy(
                isLoadingDetails = true,
                detailsError = null,
                selectedOrderDetails = null
            )

            runCatching {
                val details = ordersRepository.getOrderDetails(orderId)
                val clients = clientsRepository.getClients().associateBy { it.id }
                val cars = carsRepository.getCars().associateBy { it.id }
                val masters = mastersRepository.getMasters().associateBy { it.id }
                val services = servicesRepository.getServices().associateBy { it.id }
                val parts = partsRepository.getParts().associateBy { it.id }

                mapOrderDetails(
                    details = details,
                    clients = clients.mapValues { it.value.fullName },
                    cars = cars.mapValues { "${it.value.brand} ${it.value.model}" },
                    masters = masters.mapValues { it.value.fullName },
                    services = services.mapValues { it.value.name },
                    parts = parts.mapValues { it.value.name }
                )
            }.onSuccess { detailsUi ->
                _state.value = _state.value.copy(
                    selectedOrderDetails = detailsUi,
                    isLoadingDetails = false,
                    detailsError = null
                )
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    selectedOrderDetails = null,
                    isLoadingDetails = false,
                    detailsError = throwable.message ?: "Не удалось загрузить детали заказа"
                )
            }
        }
    }

    private fun openCreateDialog() {
        scope.launch {
            runCatching {
                cachedClients = clientsRepository.getClients()
                cachedCars = carsRepository.getCars()
                cachedMasters = mastersRepository.getMasters()
            }.onSuccess {
                _state.value = _state.value.copy(
                    isCreateDialogOpen = true,
                    createForm = CreateOrderForm(),
                    clientOptions = cachedClients.map {
                        ReferenceOptionUi(it.id, "#${it.id} ${it.fullName}")
                    },
                    masterOptions = cachedMasters.map {
                        ReferenceOptionUi(it.id, "#${it.id} ${it.fullName}")
                    },
                    carOptionsForSelectedClient = emptyList()
                )
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isCreateDialogOpen = true,
                    createForm = CreateOrderForm(
                        errorMessage = throwable.message ?: "Не удалось загрузить данные для формы"
                    ),
                    clientOptions = emptyList(),
                    masterOptions = emptyList(),
                    carOptionsForSelectedClient = emptyList()
                )
            }
        }
    }

    private fun closeCreateDialog() {
        _state.value = _state.value.copy(
            isCreateDialogOpen = false,
            createForm = CreateOrderForm(),
            carOptionsForSelectedClient = emptyList()
        )
    }

    private fun changeCreateClient(clientId: Long?) {
        val filteredCars = cachedCars
            .filter { it.clientId == clientId }
            .map {
                ReferenceOptionUi(it.id, "#${it.id} ${it.brand} ${it.model}")
            }

        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(
                clientId = clientId,
                carId = null,
                errorMessage = null
            ),
            carOptionsForSelectedClient = filteredCars
        )
    }

    private fun changeCreateCar(carId: Long?) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(
                carId = carId,
                errorMessage = null
            )
        )
    }

    private fun changeCreateMaster(masterId: Long?) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(
                masterId = masterId,
                errorMessage = null
            )
        )
    }

    private fun changeCreateDescription(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(
                description = value,
                errorMessage = null
            )
        )
    }

    private fun changeCreateComment(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(
                comment = value,
                errorMessage = null
            )
        )
    }

    private fun changeCreateStatus(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(
                status = value,
                errorMessage = null
            )
        )
    }

    private fun changeCreatePlannedCompletionAt(value: String) {
        _state.value = _state.value.copy(
            createForm = _state.value.createForm.copy(
                plannedCompletionAt = value,
                errorMessage = null
            )
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

            form.carId == null -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(errorMessage = "Выбери автомобиль")
                )
                return
            }

            form.plannedCompletionAt.isNotBlank() && !isValidIsoDateTime(form.plannedCompletionAt) -> {
                _state.value = _state.value.copy(
                    createForm = form.copy(
                        errorMessage = "Дата должна быть в формате 2026-03-23T18:30:00"
                    )
                )
                return
            }
        }

        scope.launch {
            _state.value = _state.value.copy(
                createForm = _state.value.createForm.copy(
                    isSubmitting = true,
                    errorMessage = null
                )
            )

            runCatching {
                ordersRepository.createOrder(
                    CreateOrderRequest(
                        clientId = form.clientId,
                        carId = form.carId,
                        masterId = form.masterId,
                        description = form.description.trim().ifBlank { null },
                        comment = form.comment.trim().ifBlank { null },
                        status = form.status,
                        plannedCompletionAt = form.plannedCompletionAt.trim().ifBlank { null }
                    )
                )
            }.onSuccess { createdOrder ->
                _state.value = _state.value.copy(
                    isCreateDialogOpen = false,
                    createForm = CreateOrderForm(),
                    carOptionsForSelectedClient = emptyList()
                )
                loadOrders(selectOrderId = createdOrder.id)
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    createForm = _state.value.createForm.copy(
                        isSubmitting = false,
                        errorMessage = throwable.message ?: "Не удалось создать заказ"
                    )
                )
            }
        }
    }

    private fun openAddServiceDialog() {
        val orderId = _state.value.selectedOrderId ?: return

        scope.launch {
            runCatching {
                cachedServices = servicesRepository.getServices()
            }.onSuccess {
                _state.value = _state.value.copy(
                    isAddServiceDialogOpen = true,
                    addServiceForm = CreateOrderItemForm(),
                    serviceOptions = cachedServices.map {
                        ReferenceOptionUi(it.id, "#${it.id} ${it.name}")
                    }
                )
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isAddServiceDialogOpen = true,
                    addServiceForm = CreateOrderItemForm(
                        errorMessage = throwable.message ?: "Не удалось загрузить услуги"
                    ),
                    serviceOptions = emptyList()
                )
            }
        }
    }

    private fun closeAddServiceDialog() {
        _state.value = _state.value.copy(
            isAddServiceDialogOpen = false,
            addServiceForm = CreateOrderItemForm()
        )
    }

    private fun changeAddServiceSelected(serviceId: Long?) {
        val selectedService = cachedServices.firstOrNull { it.id == serviceId }

        _state.value = _state.value.copy(
            addServiceForm = _state.value.addServiceForm.copy(
                selectedId = serviceId,
                priceAtOrder = selectedService?.basePrice.orEmpty(),
                errorMessage = null
            )
        )
    }

    private fun changeAddServiceQuantity(value: String) {
        _state.value = _state.value.copy(
            addServiceForm = _state.value.addServiceForm.copy(
                quantity = value,
                errorMessage = null
            )
        )
    }

    private fun changeAddServicePrice(value: String) {
        _state.value = _state.value.copy(
            addServiceForm = _state.value.addServiceForm.copy(
                priceAtOrder = value,
                errorMessage = null
            )
        )
    }

    private fun submitAddService() {
        val orderId = _state.value.selectedOrderId ?: return
        val form = _state.value.addServiceForm
        val serviceId = form.selectedId
        val quantity = form.quantity.toIntOrNull()

        when {
            serviceId == null -> {
                _state.value = _state.value.copy(
                    addServiceForm = form.copy(errorMessage = "Выбери услугу")
                )
                return
            }

            quantity == null || quantity < 1 -> {
                _state.value = _state.value.copy(
                    addServiceForm = form.copy(errorMessage = "Количество должно быть не меньше 1")
                )
                return
            }
        }

        scope.launch {
            _state.value = _state.value.copy(
                addServiceForm = _state.value.addServiceForm.copy(
                    isSubmitting = true,
                    errorMessage = null
                )
            )

            runCatching {
                ordersRepository.addServiceToOrder(
                    orderId = orderId,
                    request = AddOrderServiceRequest(
                        serviceId = serviceId,
                        quantity = quantity,
                        priceAtOrder = form.priceAtOrder.trim().ifBlank { null }
                    )
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    isAddServiceDialogOpen = false,
                    addServiceForm = CreateOrderItemForm()
                )
                loadOrderDetails(orderId)
                loadOrders(selectOrderId = orderId)
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    addServiceForm = _state.value.addServiceForm.copy(
                        isSubmitting = false,
                        errorMessage = throwable.message ?: "Не удалось добавить услугу"
                    )
                )
            }
        }
    }

    private fun openAddPartDialog() {
        val orderId = _state.value.selectedOrderId ?: return

        scope.launch {
            runCatching {
                cachedParts = partsRepository.getParts()
            }.onSuccess {
                _state.value = _state.value.copy(
                    isAddPartDialogOpen = true,
                    addPartForm = CreateOrderItemForm(),
                    partOptions = cachedParts.map {
                        ReferenceOptionUi(it.id, "#${it.id} ${it.name} (${it.stockQuantity} шт.)")
                    }
                )
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isAddPartDialogOpen = true,
                    addPartForm = CreateOrderItemForm(
                        errorMessage = throwable.message ?: "Не удалось загрузить запчасти"
                    ),
                    partOptions = emptyList()
                )
            }
        }
    }

    private fun closeAddPartDialog() {
        _state.value = _state.value.copy(
            isAddPartDialogOpen = false,
            addPartForm = CreateOrderItemForm()
        )
    }

    private fun changeAddPartSelected(partId: Long?) {
        val selectedPart = cachedParts.firstOrNull { it.id == partId }

        _state.value = _state.value.copy(
            addPartForm = _state.value.addPartForm.copy(
                selectedId = partId,
                priceAtOrder = selectedPart?.price.orEmpty(),
                errorMessage = null
            )
        )
    }

    private fun changeAddPartQuantity(value: String) {
        _state.value = _state.value.copy(
            addPartForm = _state.value.addPartForm.copy(
                quantity = value,
                errorMessage = null
            )
        )
    }

    private fun changeAddPartPrice(value: String) {
        _state.value = _state.value.copy(
            addPartForm = _state.value.addPartForm.copy(
                priceAtOrder = value,
                errorMessage = null
            )
        )
    }

    private fun submitAddPart() {
        val orderId = _state.value.selectedOrderId ?: return
        val form = _state.value.addPartForm
        val partId = form.selectedId
        val quantity = form.quantity.toIntOrNull()

        when {
            partId == null -> {
                _state.value = _state.value.copy(
                    addPartForm = form.copy(errorMessage = "Выбери запчасть")
                )
                return
            }

            quantity == null || quantity < 1 -> {
                _state.value = _state.value.copy(
                    addPartForm = form.copy(errorMessage = "Количество должно быть не меньше 1")
                )
                return
            }
        }

        scope.launch {
            _state.value = _state.value.copy(
                addPartForm = _state.value.addPartForm.copy(
                    isSubmitting = true,
                    errorMessage = null
                )
            )

            runCatching {
                ordersRepository.addPartToOrder(
                    orderId = orderId,
                    request = AddOrderPartRequest(
                        partId = partId,
                        quantity = quantity,
                        priceAtOrder = form.priceAtOrder.trim().ifBlank { null }
                    )
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    isAddPartDialogOpen = false,
                    addPartForm = CreateOrderItemForm()
                )
                loadOrderDetails(orderId)
                loadOrders(selectOrderId = orderId)
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    addPartForm = _state.value.addPartForm.copy(
                        isSubmitting = false,
                        errorMessage = throwable.message ?: "Не удалось добавить запчасть"
                    )
                )
            }
        }
    }

    private fun openAddPaymentDialog() {
        _state.value = _state.value.copy(
            isAddPaymentDialogOpen = true,
            addPaymentForm = CreateOrderPaymentForm()
        )
    }

    private fun closeAddPaymentDialog() {
        _state.value = _state.value.copy(
            isAddPaymentDialogOpen = false,
            addPaymentForm = CreateOrderPaymentForm()
        )
    }

    private fun changeAddPaymentAmount(value: String) {
        _state.value = _state.value.copy(
            addPaymentForm = _state.value.addPaymentForm.copy(
                amount = value,
                errorMessage = null
            )
        )
    }

    private fun changeAddPaymentMethod(value: String) {
        _state.value = _state.value.copy(
            addPaymentForm = _state.value.addPaymentForm.copy(
                paymentMethod = value,
                errorMessage = null
            )
        )
    }

    private fun changeAddPaymentStatus(value: String) {
        _state.value = _state.value.copy(
            addPaymentForm = _state.value.addPaymentForm.copy(
                paymentStatus = value,
                errorMessage = null
            )
        )
    }

    private fun submitAddPayment() {
        val orderId = _state.value.selectedOrderId ?: return
        val form = _state.value.addPaymentForm

        if (form.amount.isBlank()) {
            _state.value = _state.value.copy(
                addPaymentForm = form.copy(errorMessage = "Введи сумму")
            )
            return
        }

        scope.launch {
            _state.value = _state.value.copy(
                addPaymentForm = _state.value.addPaymentForm.copy(
                    isSubmitting = true,
                    errorMessage = null
                )
            )

            runCatching {
                ordersRepository.addPaymentToOrder(
                    orderId = orderId,
                    request = AddOrderPaymentRequest(
                        amount = form.amount.trim(),
                        paymentMethod = form.paymentMethod,
                        paymentStatus = form.paymentStatus
                    )
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    isAddPaymentDialogOpen = false,
                    addPaymentForm = CreateOrderPaymentForm()
                )
                loadOrderDetails(orderId)
                loadOrders(selectOrderId = orderId)
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    addPaymentForm = _state.value.addPaymentForm.copy(
                        isSubmitting = false,
                        errorMessage = throwable.message ?: "Не удалось добавить оплату"
                    )
                )
            }
        }
    }

    private fun mapOrderDetails(
        details: OrderDetailsDto,
        clients: Map<Long, String>,
        cars: Map<Long, String>,
        masters: Map<Long, String>,
        services: Map<Long, String>,
        parts: Map<Long, String>
    ): OrderDetailsUi {
        val order = details.order
        val clientName = clients[order.clientId] ?: "Неизвестный клиент"
        val carName = cars[order.carId] ?: "Неизвестное авто"
        val masterName = order.masterId?.let { masters[it] }

        return OrderDetailsUi(
            id = order.id,
            clientDisplay = "#${order.clientId} ${toShortName(clientName)}",
            carDisplay = "#${order.carId} $carName",
            masterDisplay = if (order.masterId != null) {
                "#${order.masterId} ${masterName?.let(::toShortName) ?: "Неизвестный мастер"}"
            } else {
                "-"
            },
            status = formatOrderStatus(order.status),
            createdAt = formatDateTime(order.createdAt),
            plannedCompletionAt = order.plannedCompletionAt?.let(::formatDateTime) ?: "-",
            completedAt = order.completedAt?.let(::formatDateTime) ?: "-",
            totalAmount = order.totalAmount,
            description = order.description ?: "-",
            comment = order.comment ?: "-",
            services = details.services.map { item ->
                OrderServiceUi(
                    serviceDisplay = "#${item.serviceId} ${services[item.serviceId] ?: "Неизвестная услуга"}",
                    quantity = item.quantity,
                    priceAtOrder = item.priceAtOrder
                )
            },
            parts = details.parts.map { item ->
                OrderPartUi(
                    partDisplay = "#${item.partId} ${parts[item.partId] ?: "Неизвестная запчасть"}",
                    quantity = item.quantity,
                    priceAtOrder = item.priceAtOrder
                )
            },
            payments = details.payments.map { payment ->
                OrderPaymentUi(
                    id = payment.id,
                    amount = payment.amount,
                    paymentMethod = formatPaymentMethod(payment.paymentMethod),
                    paymentStatus = formatPaymentStatus(payment.paymentStatus),
                    paidAt = payment.paidAt?.let(::formatDateTime) ?: "-"
                )
            }
        )
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

    private fun formatDateTime(raw: String): String {
        return try {
            val normalized = raw.replace(" ", "T")
            val dateTime = LocalDateTime.parse(normalized)
            val day = dateTime.dayOfMonth.toString().padStart(2, '0')
            val month = dateTime.monthValue.toString().padStart(2, '0')
            val year = dateTime.year
            val hour = dateTime.hour.toString().padStart(2, '0')
            val minute = dateTime.minute.toString().padStart(2, '0')
            "$day.$month.$year $hour:$minute"
        } catch (_: Exception) {
            raw
        }
    }

    private fun isValidIsoDateTime(value: String): Boolean {
        return try {
            LocalDateTime.parse(value)
            true
        } catch (_: Exception) {
            false
        }
    }
}