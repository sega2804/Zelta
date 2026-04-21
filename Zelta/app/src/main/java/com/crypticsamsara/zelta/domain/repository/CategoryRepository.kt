package com.crypticsamsara.zelta.domain.repository

import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    // writes
    suspend fun addCategory(category: Category): ZeltaResult<Unit>
    suspend fun deleteCategory(id: String): ZeltaResult<Unit>


    // Read
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: String): Category?
}