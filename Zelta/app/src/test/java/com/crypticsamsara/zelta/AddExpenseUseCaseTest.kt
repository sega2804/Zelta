package com.crypticsamsara.zelta

import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.usecase.AddExpenseUseCase
import com.crypticsamsara.zelta.domain.repository.BudgetRepository
import com.crypticsamsara.zelta.domain.repository.ExpenseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddExpenseUseCaseTest {

    private lateinit var useCase: AddExpenseUseCase
    private val expenseRepository = mockk<ExpenseRepository>()
    private val budgetRepository  = mockk<BudgetRepository>()

    @Before
    fun setup() {
        useCase = AddExpenseUseCase(expenseRepository, budgetRepository)

        coEvery {
            expenseRepository.addExpense(any())
        } returns ZeltaResult.Success(Unit)

        coEvery {
            budgetRepository.incrementSpending(any(), any(), any())
        } returns ZeltaResult.Success(Unit)
    }

    @Test
    fun `returns error when amount is zero`() = runTest {
        val result = useCase(
            amount     = 0.0,
            categoryId = "food",
            note       = "Test"
        )
        assertTrue(result is ZeltaResult.Error)
    }

    @Test
    fun `returns error when amount is negative`() = runTest {
        val result = useCase(
            amount     = -10.0,
            categoryId = "food",
            note       = "Test"
        )
        assertTrue(result is ZeltaResult.Error)
    }

    @Test
    fun `returns error when categoryId is blank`() = runTest {
        val result = useCase(
            amount     = 10.0,
            categoryId = "",
            note       = "Test"
        )
        assertTrue(result is ZeltaResult.Error)
    }

    @Test
    fun `saves expense and updates budget on success`() = runTest {
        val result = useCase(
            amount     = 25.0,
            categoryId = "food",
            note       = "Lunch"
        )

        assertTrue(result is ZeltaResult.Success)

        // Verify budget was updated
        coVerify {
            budgetRepository.incrementSpending(
                categoryId = "food",
                monthYear  = any(),
                amount     = 25.0
            )
        }
    }

    @Test
    fun `does not update budget when expense save fails`() = runTest {
        coEvery {
            expenseRepository.addExpense(any())
        } returns ZeltaResult.Error("DB error")

        useCase(amount = 25.0, categoryId = "food", note = "Lunch")

        coVerify(exactly = 0) {
            budgetRepository.incrementSpending(any(), any(), any())
        }
    }
}