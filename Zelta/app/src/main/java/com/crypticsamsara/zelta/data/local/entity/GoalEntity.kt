package com.crypticsamsara.zelta.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: String?,          // stored as "2026-12-31", null if no deadline
    val isCompleted: Boolean,
    val createdAt: String
)