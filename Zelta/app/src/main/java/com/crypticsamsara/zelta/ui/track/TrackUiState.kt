package com.crypticsamsara.zelta.ui.track

import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.domain.model.Expense
import java.time.YearMonth

data class TrackUiState(
    val isLoading: Boolean = true,
    val expenses: List<Expense> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedFilter: ExpenseFilter = ExpenseFilter.THIS_MONTH,
    val selectedCategoryId: String? = null,
    val currentMonth: YearMonth = YearMonth.now(),
    val totalForPeriod: Double = 0.0,
    val errorMessage: String? = null,

    // Expense sheet state
    val isAddSheetVisible: Boolean = false,
    val isAddingExpense: Boolean = false,
    val addExpenseSuccess: Boolean = false
)

enum class ExpenseFilter(val label: String) {
    THIS_MONTH("This Month"),
    THIS_WEEK("This Week"),
    ALL("All Time")
}

// group expenses by date for display
data class ExpenseGroup(
    val dateLabel: String,
    val expenses: List<Expense>,
    val total: Double = expenses.sumOf { it.amount }
)
