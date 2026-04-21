package com.crypticsamsara.zelta.di

import com.crypticsamsara.zelta.domain.repository.BudgetRepository
import com.crypticsamsara.zelta.domain.repository.BudgetRepositoryImpl
import com.crypticsamsara.zelta.domain.repository.CategoryRepository
import com.crypticsamsara.zelta.domain.repository.CategoryRepositoryImpl
import com.crypticsamsara.zelta.domain.repository.ExpenseRepository
import com.crypticsamsara.zelta.domain.repository.ExpenseRepositoryImpl
import com.crypticsamsara.zelta.domain.repository.GoalRepository
import com.crypticsamsara.zelta.domain.repository.GoalRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExpanseRepository(
        impl: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        impl: GoalRepositoryImpl
    ): GoalRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        impl: BudgetRepositoryImpl
    ): BudgetRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository


}