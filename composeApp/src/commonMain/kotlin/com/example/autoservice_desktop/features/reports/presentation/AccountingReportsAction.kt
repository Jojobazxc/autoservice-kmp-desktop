package com.example.autoservice_desktop.features.reports.presentation

internal sealed interface AccountingReportsAction {
    data object LoadClients : AccountingReportsAction
    data class ChangeReportType(val value: AccountingReportType) : AccountingReportsAction
    data class ChangeFrom(val value: String) : AccountingReportsAction
    data class ChangeTo(val value: String) : AccountingReportsAction
    data class ChangeStatus(val value: String) : AccountingReportsAction
    data class ChangeMethod(val value: String) : AccountingReportsAction
    data class ChangeClientId(val value: String) : AccountingReportsAction
    data class ChangeOrderId(val value: String) : AccountingReportsAction
    data class ChangeOrderStatus(val value: String) : AccountingReportsAction
    data object Generate : AccountingReportsAction
    data object ClearFilters : AccountingReportsAction
    data object NextPage : AccountingReportsAction
    data object PreviousPage : AccountingReportsAction
}
