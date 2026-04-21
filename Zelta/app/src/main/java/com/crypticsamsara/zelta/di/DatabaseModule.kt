package com.crypticsamsara.zelta.di

import android.content.Context
import androidx.room.Room
import com.crypticsamsara.zelta.data.local.ZeltaDatabase
import com.crypticsamsara.zelta.data.local.dao.BudgetDao
import com.crypticsamsara.zelta.data.local.dao.CategoryDao
import com.crypticsamsara.zelta.data.local.dao.ExpenseDao
import com.crypticsamsara.zelta.data.local.dao.GoalDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideZeltaDatabase(
        @ApplicationContext context: Context
    ): ZeltaDatabase = Room.databaseBuilder(
        context,
        ZeltaDatabase::class.java,
        "zelta_database"
    )
        .addCallback(ZeltaDatabase.getPrepopulateCallback())
        .fallbackToDestructiveMigration(false)
        .build()

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