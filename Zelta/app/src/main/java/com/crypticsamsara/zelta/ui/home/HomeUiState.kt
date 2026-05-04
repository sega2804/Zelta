package com.crypticsamsara.zelta.ui.home

import com.crypticsamsara.zelta.data.local.dao.CategoryTotal
import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.model.Goal
import com.crypticsamsara.zelta.domain.model.SyncState
import com.crypticsamsara.zelta.domain.usecase.FinanceScore
import java.time.LocalTime
import java.time.YearMonth

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val currentMonth: YearMonth = YearMonth.now(),

    // Spending
    val totalSpentThisMonth: Double = 0.0,
    val recentExpenses: List<Expense> = emptyList(),
    val categoryTotals: List<CategoryTotal> = emptyList(),

    // Goals
    val activeGoals: List<Goal> = emptyList(),

    // Budgets
    val budgets: List<Budget> = emptyList(),

    // Finance Score
    val financeScore: FinanceScore? = null,

    // error
    val errorMessage: String? = null,

    // sync state
    val syncState: SyncState = SyncState.SYNCED
) {
    // derived - total budget limit for the month
    val totalBudgetLimit: Double
        get() = budgets.sumOf { it.limitAmount }

    // derived - overall budget usage percent
    val overallBudgetUsage: Float
        get() = if (totalBudgetLimit == 0.0) 0f
        else (totalSpentThisMonth / totalBudgetLimit)
            .toFloat()
            .coerceIn(0f, 1f)

    // Derived - greeting based on time of the day
    val greeting: String
        get() {
            val hour = LocalTime.now().hour
            return when {
                hour < 12 -> "Good Morning"
                hour < 17 -> "Good Afternoon"
                else -> "Good Evening"
            }
        }
}
