package com.crypticsamsara.zelta.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.crypticsamsara.zelta.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface BudgetDao {

    // writes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Query( "DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudget(id: String)

    // Reads
    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    fun getBudgetsByMonth(monthYear: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND monthYear = :monthYear")
    fun getBudgetForCategory(
        categoryId: String,
        monthYear: String
    ): Flow<BudgetEntity?>

    // Spending Update
    @Query("""
        UPDATE budgets
        SET spentAmount = spentAmount + :amount
        WHERE categoryId = :categoryId AND monthYear = :monthYear
    """)
    suspend fun incrementSpending(
        categoryId: String,
        monthYear: String,
        amount: Double
    )

    @Query("""
        UPDATE budgets
        SET spentAmount = spentAmount - :amount
        WHERE categoryId = :categoryId AND monthYear = :monthYear
    """)
    suspend fun decrementSpending(
        categoryId: String,
        monthYear: String,
        amount: Double
    )

    // Monthly reset
    @Query("UPDATE budgets SET spentAmount = 0 WHERE monthYear = :monthYear")
    suspend fun resetMonthlySpending(monthYear: String)

}