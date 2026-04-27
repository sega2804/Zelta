package com.crypticsamsara.zelta

import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.model.Goal
import com.crypticsamsara.zelta.domain.model.SyncState
import com.crypticsamsara.zelta.domain.usecase.CalculateFinanceScoreUseCase
import com.crypticsamsara.zelta.domain.usecase.ScoreGrade
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class CalculateFinanceScoreUseCaseTest {

    private lateinit var useCase: CalculateFinanceScoreUseCase

    @Before
    fun setup() {
        useCase = CalculateFinanceScoreUseCase()
    }

    @Test
    fun `perfect score when all budgets healthy and goals have progress`() {
        val budgets = listOf(
            Budget("1", "food", currentMonth(), 300.0, 150.0),
            Budget("2", "transport", currentMonth(), 100.0, 40.0)
        )
        val goals = listOf(
            Goal("1", "Japan Trip", "✈️", 1000.0, 500.0, null),
            Goal("2", "Laptop", "💻", 1200.0, 600.0, null)
        )
        val expenses = buildExpenses(15)

        val result = useCase(expenses, budgets, goals)

        assertTrue("Score should be above 70", result.score >= 70)
        assertTrue(
            "Grade should be Good or Excellent",
            result.grade == ScoreGrade.GOOD || result.grade == ScoreGrade.EXCELLENT
        )
    }

    @Test
    fun `low score when all budgets exceeded`() {
        val budgets = listOf(
            Budget("1", "food", currentMonth(), 100.0, 200.0),
            Budget("2", "transport", currentMonth(), 50.0, 120.0)
        )
        val goals   = emptyList<Goal>()
        val expenses = emptyList<Expense>()

        val result = useCase(expenses, budgets, goals)

        assertTrue("Score should be below 50", result.score < 50)
    }

    @Test
    fun `score is always between 0 and 100`() {
        val result = useCase(
            expenses = emptyList(),
            budgets  = emptyList(),
            goals    = emptyList()
        )

        assertTrue(result.score in 0..100)
    }

    @Test
    fun `tip is never empty`() {
        val result = useCase(
            expenses = emptyList(),
            budgets  = emptyList(),
            goals    = emptyList()
        )

        assertTrue(result.tip.isNotBlank())
    }

    // Helpers
    private fun currentMonth() = YearMonth.now().toString()

    private fun buildExpenses(count: Int): List<Expense> =
        (1..count).map { i ->
            Expense(
                id = "expense_$i",
                amount = 10.0 * i,
                categoryId = "food",
                note = "Test $i",
                date = LocalDate.now().minusDays(i.toLong()),
                syncState = SyncState.SYNCED,
                localModified = Instant.now()
            )
        }
}