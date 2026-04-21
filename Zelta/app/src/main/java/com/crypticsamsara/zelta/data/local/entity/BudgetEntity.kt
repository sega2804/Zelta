package com.crypticsamsara.zelta.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    indices = [
        Index(value = ["categoryId", "monthYear"], unique = true)
    ]
)

data class BudgetEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val monthYear: String,          // "2026-04"
    val limitAmount: Double,
    val spentAmount: Double = 0.0
)