package com.crypticsamsara.zelta.domain.model

import java.time.LocalDate

data class Goal (
    val id: String,
    val name: String,
    val icon: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: LocalDate?,
    val isCompleted: Boolean = false,
    val createdAt: LocalDate = LocalDate.now()
) {
    // Derived properties
    val progressPercent: Float
        get() = if (targetAmount == 0.0) 0f
                else (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f)

    val remainingAmount: Double
        get() = (targetAmount - currentAmount).coerceAtLeast(0.0)

    val isOverdue: Boolean
        get() = deadline != null &&
                LocalDate.now().isAfter(deadline) &&
                !isCompleted
}