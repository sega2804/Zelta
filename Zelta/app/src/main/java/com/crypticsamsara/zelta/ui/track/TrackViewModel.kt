package com.crypticsamsara.zelta.ui.track

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.repository.CategoryRepository
import com.crypticsamsara.zelta.domain.usecase.AddExpenseUseCase
import com.crypticsamsara.zelta.domain.usecase.DeleteExpenseUseCase
import com.crypticsamsara.zelta.domain.usecase.GetAllExpensesUseCase
import com.crypticsamsara.zelta.domain.usecase.GetExpensesByMonthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val getAllExpenses: GetAllExpensesUseCase,
    private val getExpensesByMonth: GetExpensesByMonthUseCase,
    private val addExpense: AddExpenseUseCase,
    private val deleteExpense: DeleteExpenseUseCase,
    private val categoryRepository: CategoryRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(TrackUiState())
    val uiState: StateFlow<TrackUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        observeExpenses()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    private fun observeExpenses() {
        viewModelScope.launch {
            getExpensesByMonth(YearMonth.now().toString()).collect { expenses ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        expenses = expenses,
                        totalForPeriod = expenses.sumOf { e -> e.amount }
                    )
                }
            }
        }
    }

    fun onFilterSelected(filter: ExpenseFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        viewModelScope.launch {
            when (filter) {
                ExpenseFilter.THIS_MONTH -> {
                    getExpensesByMonth(YearMonth.now().toString())
                        .collect { expenses ->
                            _uiState.update {
                                it.copy(
                                    expenses       = expenses,
                                    totalForPeriod = expenses.sumOf { e -> e.amount }
                                )
                            }
                        }
                }
                ExpenseFilter.THIS_WEEK -> {
                    val weekStart = LocalDate.now().minusDays(7).toString()
                    val today     = LocalDate.now().toString()
                    // Filter locally for this week
                    _uiState.update { state ->
                        val filtered = state.expenses.filter {
                            it.date.toString() in weekStart..today
                        }
                        state.copy(
                            expenses       = filtered,
                            totalForPeriod = filtered.sumOf { it.amount }
                        )
                    }
                }
                ExpenseFilter.ALL -> {
                    getAllExpenses().collect { expenses ->
                        _uiState.update {
                            it.copy(
                                expenses       = expenses,
                                totalForPeriod = expenses.sumOf { e -> e.amount }
                            )
                        }
                    }
                }
            }
        }
    }

    fun onCategoryFilterSelected(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun showAddSheet() {
        _uiState.update { it.copy(isAddSheetVisible = true) }
    }

    fun hideAddSheet() {
        _uiState.update { it.copy(
            isAddSheetVisible  = false,
            addExpenseSuccess  = false
        )}
    }

    fun onAddExpense(
        amount: Double,
        categoryId: String,
        note: String,
        date: LocalDate
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingExpense = true) }

            val result = addExpense(
                amount     = amount,
                categoryId = categoryId,
                note       = note,
                date       = date
            )

            when (result) {
                is ZeltaResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isAddingExpense   = false,
                            addExpenseSuccess  = true,
                            isAddSheetVisible = false
                        )
                    }
                }
                is ZeltaResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isAddingExpense = false,
                            errorMessage    = result.message
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    fun onDeleteExpense(expense: Expense) {
        viewModelScope.launch {
            deleteExpense(expense)
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Group expenses by date for display
    fun groupExpensesByDate(expenses: List<Expense>): List<ExpenseGroup> {
        val today     = LocalDate.now()
        val yesterday = today.minusDays(1)

        return expenses
            .groupBy { it.date }
            .toSortedMap(compareByDescending { it })
            .map { (date, exps) ->
                val label = when (date) {
                    today     -> "Today"
                    yesterday -> "Yesterday"
                    else      -> date.format(
                        DateTimeFormatter.ofPattern("EEEE, MMM d")
                    )
                }
                ExpenseGroup(dateLabel = label, expenses = exps)
            }
    }
}