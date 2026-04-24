package com.crypticsamsara.zelta.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [
        Index(value = ["date"]),
        Index(value = ["categoryId"]),
        Index(value = ["syncState"])
    ]
)

data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val categoryId: String,
    val note: String,
    val date: String,
    val syncState: String, // stored as SyncState.name
    val localModified: Long, // Store as epoch millis for easier Room compatibility
    val cloudModified: Long? = null // Store as epoch millis for easier Room compatibility
) {

}