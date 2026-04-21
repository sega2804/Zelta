package com.crypticsamsara.zelta.data.local.entity

import androidx.compose.ui.graphics.Color
import com.crypticsamsara.zelta.domain.model.SyncState
import com.crypticsamsara.zelta.domain.model.Budget
import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.domain.model.Expense
import com.crypticsamsara.zelta.domain.model.Goal
import java.time.Instant
import java.time.LocalDate
import androidx.core.graphics.toColorInt

// Expense Mappers
fun ExpenseEntity.toDomain() = Expense(
    id            = id,
    amount        = amount,
    categoryId    = categoryId,
    note          = note,
    date          = LocalDate.parse(date),
    syncState     = SyncState.valueOf(syncState),
    localModified = Instant.ofEpochMilli(localModified),
    cloudModified = cloudModified?.let { Instant.ofEpochMilli(it) }
)

fun Expense.toEntity() = ExpenseEntity(
    id            = id,
    amount        = amount,
    categoryId    = categoryId,
    note          = note,
    date          = date.toString(),
    syncState     = syncState.name,
    localModified = localModified.toEpochMilli(),
    cloudModified = cloudModified?.toEpochMilli()
)

//Goal Mappers
fun GoalEntity.toDomain() = Goal(
    id            = id,
    name          = name,
    icon          = icon,
    targetAmount  = targetAmount,
    currentAmount = currentAmount,
    deadline      = deadline?.let { LocalDate.parse(it) },
    isCompleted   = isCompleted,
    createdAt     = LocalDate.parse(createdAt)
)

fun Goal.toEntity() = GoalEntity(
    id            = id,
    name          = name,
    icon          = icon,
    targetAmount  = targetAmount,
    currentAmount = currentAmount,
    deadline      = deadline?.toString(),
    isCompleted   = isCompleted,
    createdAt     = createdAt.toString()
)

// Budget Mappers
fun BudgetEntity.toDomain() = Budget(
    id           = id,
    categoryId   = categoryId,
    monthYear    = monthYear,
    limitAmount  = limitAmount,
    spentAmount  = spentAmount
)

fun Budget.toEntity() = BudgetEntity(
    id           = id,
    categoryId   = categoryId,
    monthYear    = monthYear,
    limitAmount  = limitAmount,
    spentAmount  = spentAmount
)

//Category Mappers
fun CategoryEntity.toDomain() = Category(
    id        = id,
    name      = name,
    icon      = icon,
    color     = Color(colorHex.toColorInt()),
    isDefault = isDefault
)

fun Category.toEntity() = CategoryEntity(
    id        = id,
    name      = name,
    icon      = icon,
    colorHex  = "#%06X".format(0xFFFFFF and color.hashCode()),
    isDefault = isDefault
)