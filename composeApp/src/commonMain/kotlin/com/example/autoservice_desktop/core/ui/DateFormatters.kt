package com.example.autoservice_desktop.core.ui

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val isoDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

internal fun formatRuDate(raw: String): String {
    return runCatching {
        parseDate(raw).toRuDate()
    }.getOrElse {
        raw
    }
}

internal fun formatRuDateTime(raw: String): String {
    return runCatching {
        val dateTime = parseDateTime(raw)
        "${dateTime.toLocalDate().toRuDate()} ${dateTime.hour.twoDigits()}:${dateTime.minute.twoDigits()}"
    }.getOrElse {
        formatRuDate(raw)
    }
}

internal fun isoDateToRuDate(raw: String): String {
    return raw.ifBlank { "" }.let(::formatRuDate)
}

internal fun isoDateToEpochMillis(raw: String): Long? {
    return runCatching {
        LocalDate.parse(raw, isoDateFormatter)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    }.getOrNull()
}

internal fun epochMillisToIsoDate(value: Long): String {
    return Instant.ofEpochMilli(value)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .format(isoDateFormatter)
}

private fun parseDate(raw: String): LocalDate {
    return when {
        raw.contains("T") || raw.contains(" ") -> parseDateTime(raw).toLocalDate()
        else -> LocalDate.parse(raw, isoDateFormatter)
    }
}

private fun parseDateTime(raw: String): LocalDateTime {
    return LocalDateTime.parse(raw.replace(" ", "T"))
}

private fun LocalDate.toRuDate(): String {
    return "${dayOfMonth.twoDigits()}.${monthValue.twoDigits()}.$year"
}

private fun Int.twoDigits(): String = toString().padStart(2, '0')
