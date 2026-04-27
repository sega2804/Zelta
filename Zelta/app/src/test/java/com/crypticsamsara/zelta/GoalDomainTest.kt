package com.crypticsamsara.zelta



import com.crypticsamsara.zelta.domain.model.Goal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class GoalDomainTest {

    @Test
    fun `progressPercent is correct`() {
        val goal = buildGoal(targetAmount = 1000.0, currentAmount = 500.0)
        assertEquals(0.5f, goal.progressPercent, 0.001f)
    }

    @Test
    fun `progressPercent never exceeds 1`() {
        val goal = buildGoal(targetAmount = 100.0, currentAmount = 200.0)
        assertEquals(1.0f, goal.progressPercent, 0.001f)
    }

    @Test
    fun `remainingAmount is zero when goal exceeded`() {
        val goal = buildGoal(targetAmount = 100.0, currentAmount = 150.0)
        assertEquals(0.0, goal.remainingAmount, 0.001)
    }

    @Test
    fun `isOverdue when deadline passed and not complete`() {
        val goal = buildGoal(
            deadline    = LocalDate.now().minusDays(1),
            isCompleted = false
        )
        assertTrue(goal.isOverdue)
    }

    @Test
    fun `not overdue when completed even if deadline passed`() {
        val goal = buildGoal(
            deadline    = LocalDate.now().minusDays(1),
            isCompleted = true
        )
        assertFalse(goal.isOverdue)
    }

    @Test
    fun `not overdue when no deadline`() {
        val goal = buildGoal(deadline = null)
        assertFalse(goal.isOverdue)
    }

    // Helper
    private fun buildGoal(
        targetAmount: Double  = 1000.0,
        currentAmount: Double = 0.0,
        deadline: LocalDate?  = null,
        isCompleted: Boolean  = false
    ) = Goal(
        id = "test",
        name = "Test Goal",
        icon = "🎯",
        targetAmount = targetAmount,
        currentAmount = currentAmount,
        deadline = deadline,
        isCompleted = isCompleted
    )
}