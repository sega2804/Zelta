package com.crypticsamsara.zelta.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.crypticsamsara.zelta.data.local.dao.BudgetDao
import com.crypticsamsara.zelta.data.local.dao.CategoryDao
import com.crypticsamsara.zelta.data.local.dao.ExpenseDao
import com.crypticsamsara.zelta.data.local.dao.GoalDao
import com.crypticsamsara.zelta.data.local.entity.BudgetEntity
import com.crypticsamsara.zelta.data.local.entity.CategoryEntity
import com.crypticsamsara.zelta.data.local.entity.ExpenseEntity
import com.crypticsamsara.zelta.data.local.entity.GoalEntity
import com.crypticsamsara.zelta.data.local.entity.toEntity
import com.crypticsamsara.zelta.domain.model.DefaultCategories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExpenseEntity::class,
        GoalEntity::class,
        BudgetEntity::class,
        CategoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class ZeltaDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun goalDao(): GoalDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao

    companion object {

        @Volatile
        private var INSTANCE: ZeltaDatabase? = null

        fun getPrepopulateCallback() = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val categoryDao = database.categoryDao()
                        if (categoryDao.getCategoryCount() == 0) {
                            val entities = DefaultCategories.map { category ->
                                CategoryEntity(
                                    id = category.id,
                                    name = category.name,
                                    icon = category.icon,
                                    colorHex = "#%06X".format(
                                        0xFFFFFF and category.color.hashCode()
                                    ),
                                    isDefault = category.isDefault
                                )
                            }
                            categoryDao.insertCategories(entities)
                        }
                    }
                }
            }
        }
    }
}
