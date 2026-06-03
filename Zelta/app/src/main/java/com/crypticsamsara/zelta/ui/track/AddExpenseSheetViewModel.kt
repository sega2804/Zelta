package com.crypticsamsara.zelta.ui.track

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.repository.CategoryRepository
import com.crypticsamsara.zelta.domain.usecase.AddExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AddExpenseUiState(
    val categories : List<Category> = emptyList(),
    val isLoading  : Boolean        = false,
    val isSuccess  : Boolean        = false,
    val error      : String?        = null
)



@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpenseUseCase  : AddExpenseUseCase,
    private val categoryRepository : CategoryRepository
) : ViewModel() {

    private val _extra = MutableStateFlow(
        AddExpenseUiState()
    )

    val uiState: StateFlow<AddExpenseUiState> = combine(
        categoryRepository.getAllCategories(),
        _extra
    ) { categories, extra ->
        extra.copy(categories = categories)
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = AddExpenseUiState()
    )

    fun addExpense(
        amount     : Double,
        categoryId : String,
        note       : String,
        date       : LocalDate = LocalDate.now()
    ) {
        viewModelScope.launch {
            _extra.update { it.copy(isLoading = true, error = null) }
            when (val result = addExpenseUseCase(amount, categoryId, note, date)) {
                is ZeltaResult.Success ->
                    _extra.update { it.copy(isLoading = false, isSuccess = true) }
                is ZeltaResult.Error   ->
                    _extra.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    fun resetSuccess() = _extra.update { it.copy(isSuccess = false) }
}