package com.crypticsamsara.zelta.ui.budget

import androidx.compose.ui.graphics.Path.Companion.combine
import androidx.compose.ui.text.style.TextDecoration.Companion.combine
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.repository.CategoryRepository
import com.crypticsamsara.zelta.domain.usecase.DeleteBudgetUseCase
import com.crypticsamsara.zelta.domain.usecase.GetBudgetsByMonthUseCase
import com.crypticsamsara.zelta.domain.usecase.SetBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val getBudgetsByMonth: GetBudgetsByMonthUseCase,
    private val setBudget: SetBudgetUseCase,
    private val deleteBudget: DeleteBudgetUseCase,
    private val categoryRepository: CategoryRepository
    ): ViewModel() {
        private val _uiState = MutableStateFlow(BudgetUiState())
        val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

        private val currentMonthYear = YearMonth.now().toString()

    init {
        observeBudgetData()
    }

    private fun observeBudgetData() {
        viewModelScope.launch {
            combine(
                getBudgetsByMonth(currentMonthYear),
                categoryRepository.getAllCategories()
            ) { budgets, categories ->
                _uiState.value.copy(
                    isLoading = false,
                    budgets = budgets,
                    categories = categories
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    // Sheet
    fun showSetBudgetSheet(categoryId: String? = null) {
        _uiState.update {
            it.copy(
                isSetBudgetSheetVisible = true,
                selectedCategoryId = categoryId
            )
        }
    }

    fun hideSetBudgetSheet() {
        _uiState.update {
            it.copy(
                isSetBudgetSheetVisible = false,
                selectedCategoryId = null
            )
        }
    }

    // Set Budget
    fun onSetBudget(categoryId: String, limitAmount: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val result = setBudget(
                categoryId = categoryId,
                limitAmount = limitAmount,
                monthYear = currentMonthYear
            )

            when (result) {
                is ZeltaResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            isSetBudgetSheetVisible = false,
                            selectedCategoryId = null
                        )
                    }
                }

                is ZeltaResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> Unit
            }
        }
    }


    // delete func
    fun onDeleteBudget(budgetId: String) {
        viewModelScope.launch {
            deleteBudget(budgetId)
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}