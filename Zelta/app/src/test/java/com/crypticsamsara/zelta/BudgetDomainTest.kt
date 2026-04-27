package com.crypticsamsara.zelta

import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.BudgetState
import org.junit.Assert.assertEquals
import org.junit.Test

class BudgetDomainTest {

    @Test
    fun `budgetState is HEALTHY when under 80 percent`() {
        val budget = buildBudget(limitAmount = 100.0, spentAmount = 79.0)
        assertEquals(BudgetState.HEALTHY, budget.budgetState)
    }

    @Test
    fun `budgetState is WARNING when between 80 and 100 percent`() {
        val budget = buildBudget(limitAmount = 100.0, spentAmount = 85.0)
        assertEquals(BudgetState.WARNING, budget.budgetState)
    }

    @Test
    fun `budgetState is EXCEEDED when over 100 percent`() {
        val budget = buildBudget(limitAmount = 100.0, spentAmount = 110.0)
        assertEquals(BudgetState.EXCEEDED, budget.budgetState)
    }

    @Test
    fun `remainingAmount is zero when exceeded`() {
        val budget = buildBudget(limitAmount = 100.0, spentAmount = 150.0)
        assertEquals(0.0, budget.remainingAmount, 0.001)
    }

    @Test
    fun `usagePercent is capped at 1`() {
        val budget = buildBudget(limitAmount = 100.0, spentAmount = 200.0)
        assertEquals(1.0f, budget.usagePercent, 0.001f)
    }

    // Helper
    private fun buildBudget(
        limitAmount: Double,
        spentAmount: Double
    ) = Budget(
        id = "test",
        categoryId = "food",
        monthYear = "2026-04",
        limitAmount = limitAmount,
        spentAmount = spentAmount
    )
}