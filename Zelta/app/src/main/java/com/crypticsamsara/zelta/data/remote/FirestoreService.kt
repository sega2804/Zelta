package com.crypticsamsara.zelta.data.remote

import com.crypticsamsara.zelta.data.local.entity.ExpenseEntity
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.model.safeCall
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {

     // collection paths
    private fun expensesRef(uid: String) =
        firestore.collection("users").document(uid)
            .collection("expenses")

    private fun goalsRef(uid: String) =
        firestore.collection("users").document(uid)
            .collection("goals")

    // upload expense
    suspend fun uploadExpense(
        uid: String,
        expense: ExpenseEntity
    ): ZeltaResult<Unit> = safeCall {
        expensesRef(uid)
            .document(expense.id)
            .set(expense.toFirestoreMap(), SetOptions.merge())
            .await()
    }

    // upload all pending
    suspend fun uploadExpenses(
        uid: String,
        expenses: List<ExpenseEntity>
    ): ZeltaResult<Unit>
    = safeCall {
        val batch = firestore.batch()
        expenses.forEach { expense ->
            val ref = expensesRef(uid).document(expense.id)
            batch.set(ref, expense.toFirestoreMap(), SetOptions.merge())
        }
        batch.commit().await()
        }

    // fetch all expenses
    suspend fun fetchExpenses(
        uid: String
    ): ZeltaResult<List<ExpenseEntity>> = safeCall {
        val snapshot = expensesRef(uid).get().await()
        snapshot.documents.mapNotNull { doc ->
            doc.toObject(ExpenseEntity::class.java)
        }
    }

    // Delete expense
    suspend fun deleteExpense(
        uid: String,
        expenseId: String
    ): ZeltaResult<Unit> = safeCall {
        expensesRef(uid). document(expenseId).delete().await()
    }

    // Firestore serialization
    private fun ExpenseEntity.toFirestoreMap() = mapOf(
        "id" to id,
        "amount" to amount,
        "categoryId" to categoryId,
        "note" to note,
        "date" to date,
        "syncState" to "SYNCED",
        "localModified" to localModified,
        "cloudModified" to System.currentTimeMillis()
    )
}
