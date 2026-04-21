package com.crypticsamsara.zelta.domain.model

data class Budget(
    val id: String,
    val categoryId: String,
    val monthYear: String,
    val limitAmount: Double,
    val spentAmount: Double = 0.0
) {
    // Derived properties
    val remainingAmount: Double
        get() = (limitAmount - spentAmount).coerceAtLeast(0.0)

    val usagePercent: Float
        get() = if (limitAmount == 0.0) 0f
                else (spentAmount / limitAmount).toFloat().coerceIn(0f, 1f)

    val budgetState: BudgetState
        get() = when {
            usagePercent >= 1f -> BudgetState.EXCEEDED
            usagePercent >= 0.8f -> BudgetState.WARNING
            else -> BudgetState.HEALTHY
        }
}

enum class BudgetState {
    HEALTHY,
    WARNING,
    EXCEEDED
}
