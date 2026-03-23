package com.example.autoservice_desktop.features.orders.presentation

import com.example.autoservice_desktop.core.ui.formatOrderStatus
import com.example.autoservice_desktop.core.ui.formatPaymentMethod
import com.example.autoservice_desktop.core.ui.formatPaymentStatus
import com.example.autoservice_desktop.features.cars.data.CarsRepository
import com.example.autoservice_desktop.features.clients.data.ClientsRepository
import com.example.autoservice_desktop.features.masters.data.MastersRepository
import com.example.autoservice_desktop.features.orders.data.OrderDetailsDto
import com.example.autoservice_desktop.features.orders.data.OrdersRepository
import com.example.autoservice_desktop.features.parts.data.PartsRepository
import com.example.autoservice_desktop.features.services.data.ServicesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    internal fun dispatch(action: OrdersAction) {
        when (action) {
            OrdersAction.Load -> loadOrders()
            is OrdersAction.SelectOrder -> selectOrder(action.orderId)
        }
    }

    private fun loadOrders() {
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
                val firstId = items.firstOrNull()?.id

                _state.value = _state.value.copy(
                    items = items,
                    isLoadingList = false,
                    listError = null,
                    selectedOrderId = firstId
                )

                if (firstId != null) {
                    loadOrderDetails(firstId)
                } else {
                    _state.value = _state.value.copy(
                        selectedOrderDetails = null,
                        detailsError = null,
                        isLoadingDetails = false
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
            val dateTime = java.time.LocalDateTime.parse(normalized)
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
}