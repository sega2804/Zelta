package com.crypticsamsara.zelta.di


import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.crypticsamsara.zelta.data.local.ZeltaDatabase
import com.crypticsamsara.zelta.data.local.dao.BudgetDao
import com.crypticsamsara.zelta.data.local.dao.CategoryDao
import com.crypticsamsara.zelta.data.local.dao.ExpenseDao
import com.crypticsamsara.zelta.data.local.dao.GoalDao
import com.crypticsamsara.zelta.data.local.entity.CategoryEntity
import com.crypticsamsara.zelta.domain.model.DefaultCategories
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideZeltaDatabase(
        @ApplicationContext context: Context
    ): ZeltaDatabase {
        var database: ZeltaDatabase? = null

        val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Database is now created — safe to access it
                CoroutineScope(Dispatchers.IO).launch {
                    database?.categoryDao()?.let { dao ->
                        if (dao.getCategoryCount() == 0) {
                            val entities = DefaultCategories.map { category ->
                                CategoryEntity(
                                    id        = category.id,
                                    name      = category.name,
                                    icon      = category.icon,
                                    colorHex  = "#%06X".format(
                                        0xFFFFFF and category.color.hashCode()
                                    ),
                                    isDefault = category.isDefault
                                )
                            }
                            dao.insertCategories(entities)
                        }
                    }
                }
            }
        }

        database = Room.databaseBuilder(
            context,
            ZeltaDatabase::class.java,
            "zelta_database"
        )
            .addCallback(callback)
            .fallbackToDestructiveMigration()
            .build()

        return database
    }

    @Provides
    @Singleton
    fun provideExpenseDao(db: ZeltaDatabase): ExpenseDao = db.expenseDao()

    @Provides
    @Singleton
    fun provideGoalDao(db: ZeltaDatabase): GoalDao = db.goalDao()

    @Provides
    @Singleton
    fun provideBudgetDao(db: ZeltaDatabase): BudgetDao = db.budgetDao()

    @Provides
    @Singleton
    fun provideCategoryDao(db: ZeltaDatabase): CategoryDao = db.categoryDao()
}