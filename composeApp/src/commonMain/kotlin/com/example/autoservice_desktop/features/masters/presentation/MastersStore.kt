package com.example.autoservice_desktop.features.masters.presentation

import com.example.autoservice_desktop.features.masters.data.CreateMasterRequest
import com.example.autoservice_desktop.features.masters.data.MastersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class MastersStore(
    private val repository: MastersRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(MastersState())
    internal val state: StateFlow<MastersState> = _state.asStateFlow()

    internal fun dispatch(action: MastersAction) {
        when (action) {
            MastersAction.Load -> loadMasters()

            MastersAction.OpenCreateDialog -> openCreateDialog()
            MastersAction.CloseCreateDialog -> closeCreateDialog()

            is MastersAction.UpdateFullName -> updateFullName(action.value)
            is MastersAction.UpdateSpecialization -> updateSpecialization(action.value)
            is MastersAction.UpdateExperienceYears -> updateExperienceYears(action.value)
            is MastersAction.UpdatePhone -> updatePhone(action.value)
            is MastersAction.UpdateEmail -> updateEmail(action.value)
            is MastersAction.UpdateEmploymentStatus -> updateEmploymentStatus(action.value)

            MastersAction.SubmitCreate -> submitCreate()
        }
    }

    private fun loadMasters() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching { repository.getMasters() }
                .onSuccess { masters ->
                    _state.value = _state.value.copy(
                        items = masters,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        items = emptyList(),
                        isLoading = false,
                        error = throwable.message ?: "Неизвестная ошибка"
                    )
                }
        }
    }

    private fun openCreateDialog() {
        _state.value = _state.value.copy(
            isCreateDialogOpen = true,
            createError = null
        )
    }

    private fun closeCreateDialog() {
        _state.value = _state.value.copy(
            isCreateDialogOpen = false,
            isCreating = false,
            fullNameInput = "",
            specializationInput = "",
            experienceYearsInput = "",
            phoneInput = "",
            emailInput = "",
            employmentStatusInput = "ACTIVE",
            createError = null
        )
    }

    private fun updateFullName(value: String) {
        _state.value = _state.value.copy(fullNameInput = value)
    }

    private fun updateSpecialization(value: String) {
        _state.value = _state.value.copy(specializationInput = value)
    }

    private fun updateExperienceYears(value: String) {
        _state.value = _state.value.copy(experienceYearsInput = value)
    }

    private fun updatePhone(value: String) {
        _state.value = _state.value.copy(phoneInput = value)
    }

    private fun updateEmail(value: String) {
        _state.value = _state.value.copy(emailInput = value)
    }

    private fun updateEmploymentStatus(value: String) {
        _state.value = _state.value.copy(employmentStatusInput = value)
    }

    private fun submitCreate() {
        val current = _state.value

        val fullName = current.fullNameInput.trim()
        val specialization = current.specializationInput.trim().ifBlank { null }
        val experienceYears = current.experienceYearsInput.trim().ifBlank { null }?.toIntOrNull()
        val phone = current.phoneInput.trim().ifBlank { null }
        val email = current.emailInput.trim().ifBlank { null }
        val employmentStatus = current.employmentStatusInput

        if (fullName.isBlank()) {
            _state.value = current.copy(createError = "Введите ФИО")
            return
        }

        if (current.experienceYearsInput.isNotBlank() && experienceYears == null) {
            _state.value = current.copy(createError = "Стаж должен быть числом")
            return
        }

        scope.launch {
            _state.value = _state.value.copy(
                isCreating = true,
                createError = null
            )

            runCatching {
                repository.createMaster(
                    CreateMasterRequest(
                        fullName = fullName,
                        specialization = specialization,
                        experienceYears = experienceYears,
                        phone = phone,
                        email = email,
                        employmentStatus = employmentStatus
                    )
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    isCreateDialogOpen = false,
                    isCreating = false,
                    fullNameInput = "",
                    specializationInput = "",
                    experienceYearsInput = "",
                    phoneInput = "",
                    emailInput = "",
                    employmentStatusInput = "ACTIVE",
                    createError = null
                )
                loadMasters()
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isCreating = false,
                    createError = throwable.message ?: "Не удалось создать мастера"
                )
            }
        }
    }
}