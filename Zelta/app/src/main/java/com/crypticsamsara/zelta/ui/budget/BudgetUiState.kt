package com.crypticsamsara.zelta.ui.budget

import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.BudgetState
import com.crypticsamsara.zelta.domain.model.Category
import java.time.YearMonth
import kotlin.collections.filter

data class BudgetUiState(
    val isLoading: Boolean = true,
    val budgets: List<Budget> = emptyList(),
    val categories: List<Category> = emptyList(),
    val currentMonth: YearMonth = YearMonth.now(),
    val errorMessage: String? = null,

    // Sheet
    val isSetBudgetSheetVisible: Boolean = false,
    val selectedCategoryId: String? = null,
    val isSubmitting: Boolean = false
) {
    // Total budget limit across all categories
    val totalLimit: Double
        get() = budgets.sumOf { it.limitAmount }

    // Total spent across all categories
    val totalSpent: Double
        get() = budgets.sumOf { it.spentAmount }

    // Overall usage percent
    val overallUsage: Float
        get() = if (totalLimit == 0.0) 0f
        else (totalSpent / totalLimit)
            .toFloat()
            .coerceIn(0f, 1f)

    // Categories without a budget set
    val unbudgetedCategories: List<Category>
        get() {
            val budgetedIds = budgets.map { it.categoryId }.toSet()
            return categories.filter { it.id !in budgetedIds }
        }

    // Budget health summary
    val exceededCount: Int
        get() = budgets.count { it.budgetState == BudgetState.EXCEEDED }

    val warningCount: Int
        get() = budgets.count { it.budgetState == BudgetState.WARNING }
}