package com.crypticsamsara.zelta.domain.model

import java.time.LocalDate
import java.time.Instant

data class Expense(
    val id: String,
    val amount: Double,
    val categoryId: String,
    val note: String,
    val date: LocalDate,
    val syncState: SyncState = SyncState.PENDING,
    val localModified: Instant = Instant.now(),
    val cloudModified: Instant? = null
)

enum class SyncState {
    SYNCED,
    PENDING,
    FAILED
}