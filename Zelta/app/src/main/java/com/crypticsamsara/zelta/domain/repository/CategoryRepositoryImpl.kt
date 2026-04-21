package com.crypticsamsara.zelta.domain.repository

import com.crypticsamsara.zelta.data.local.dao.CategoryDao
import com.crypticsamsara.zelta.data.local.entity.toDomain
import com.crypticsamsara.zelta.data.local.entity.toEntity
import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.model.safeCall
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun addCategory
                (category: Category): ZeltaResult<Unit> = safeCall{
                    categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory
                (id: String): ZeltaResult<Unit> = safeCall{
                    categoryDao.deleteCustomCategory(id)
    }

    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
    }

    override suspend fun getCategoryById(id: String): Category? =
        categoryDao.getCategoryById(id)?.toDomain()

}