package com.crypticsamsara.zelta.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.crypticsamsara.zelta.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // writes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: String)

    // reads
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date LIKE :monthYear || '%' ORDER BY date DESC")
    fun getExpensesByMonth(monthYear: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByCategory(categoryId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesBetweenDates(startDate: String, endDate: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: String): ExpenseEntity?

    // Analytics query
    @Query("SELECT SUM(amount) FROM expenses WHERE date LIKE :monthYear || '%'")
    fun getTotalSpentByMonth(monthYear: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE categoryId = :categoryId AND date LIKE :monthYear || '%'")
    fun getTotalSpentByCategoryAndMonth(
        categoryId: String,
        monthYear: String
    ): Flow<Double?>

    @Query("""SELECT categoryId, SUM(amount) as total FROM expenses
        WHERE date LIKE :monthYear || '%'
        GROUP BY categoryId
        ORDER BY total DESC
    """)
    fun getCategoryTotalsForMonth(monthYear: String): Flow<List<CategoryTotal>>

    // Sync queries
    @Query("SELECT * FROM expenses WHERE syncState = 'PENDING'")
    suspend fun getPendingExpenses(): List<ExpenseEntity>

    @Query("UPDATE expenses SET syncState = :syncState WHERE id = :id")
    suspend fun setExpenseSynced(id: String, syncState: String)

    // Pagination
    @Query("SELECT * FROM expenses ORDER BY date DESC LIMIT :pageSize OFFSET :offset")
    fun getExpensesPaginated(pageSize: Int, offset: Int): Flow<List<ExpenseEntity>>

}

data class CategoryTotal(
    val categoryId: String,
    val total: Double
)