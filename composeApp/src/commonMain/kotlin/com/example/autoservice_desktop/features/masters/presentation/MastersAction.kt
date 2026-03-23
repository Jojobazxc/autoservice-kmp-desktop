package com.example.autoservice_desktop.features.masters.presentation

internal sealed interface MastersAction {
    data object Load : MastersAction

    data object OpenCreateDialog : MastersAction
    data object CloseCreateDialog : MastersAction

    data class UpdateFullName(val value: String) : MastersAction
    data class UpdateSpecialization(val value: String) : MastersAction
    data class UpdateExperienceYears(val value: String) : MastersAction
    data class UpdatePhone(val value: String) : MastersAction
    data class UpdateEmail(val value: String) : MastersAction
    data class UpdateEmploymentStatus(val value: String) : MastersAction

    data object SubmitCreate : MastersAction
}