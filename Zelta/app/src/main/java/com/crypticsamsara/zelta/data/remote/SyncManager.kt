package com.crypticsamsara.zelta.data.remote

import com.crypticsamsara.zelta.data.local.dao.ExpenseDao
import com.crypticsamsara.zelta.domain.model.SyncState
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val firestoreService: FirestoreService,
    private val expenseDao: ExpenseDao,
    private val firebaseAuth: FirebaseAuth
) {
    private val syncScope = CoroutineScope(Dispatchers.IO)

    // Trigger sync
    fun syncNow() {
        syncScope.launch {
            val uid = firebaseAuth.currentUser?.uid ?: return@launch
            syncPendingExpenses(uid)
        }
    }

    // upload all pending expenses
    private suspend fun syncPendingExpenses(uid: String) {
        val pending = expenseDao.getPendingExpenses()
        if (pending.isEmpty()) return

        val result = firestoreService.uploadExpenses(uid, pending)

        when(result) {
            is ZeltaResult.Success -> {
                pending.forEach { expense ->
                    expenseDao.updateSyncState(expense.id, SyncState.SYNCED.name)
                }
            }
            is ZeltaResult.Error -> {
                retryWithBackoff(uid, pending.map {it.id})
            }
            else -> Unit
        }
    }

    // fetch cloud expenses on logic
    suspend fun fetchAndMerge(uid: String) {
        val result = firestoreService.fetchExpenses(uid)

        if (result is ZeltaResult.Success) {
            // cloud overwrites local if newer
            result.data.forEach { cloudExpense ->
                val local = expenseDao.getExpenseById(cloudExpense.id)

                if (local == null) {
                    // new from cloud - insert locally
                    expenseDao.insertExpense(
                        cloudExpense.copy(syncState = SyncState.SYNCED.name)
                    )
                } else {
                    // conflict resolution
                    val cloudModified = cloudExpense.cloudModified ?: 0L
                    val localModified = local.localModified

                    if (cloudModified > localModified) {
                        expenseDao.insertExpense(
                            cloudExpense.copy(syncState = SyncState.SYNCED.name)
                        )
                    }
                }
            }
        }
    }

    // exponential backoff retry
    private suspend fun retryWithBackoff(
        uid: String,
        expenseIds: List<String>
    ) {
        var delayMs = 2000L
        repeat(3) {
            val pending = expenseDao.getPendingExpenses()
                .filter { it.id in expenseIds}

            if (pending.isEmpty()) return

            val result = firestoreService.uploadExpenses(uid, pending)
            if (result is ZeltaResult.Success) {
                expenseDao.updateSyncState(
                    expenseIds = expenseIds,
                    syncState = SyncState.SYNCED.name
                )
                return
            }
            delayMs *= 2 // increment times 2
        }

            // Mark as failed after 3 attempts
        expenseIds.forEach { id ->
            expenseDao.updateSyncState(id, SyncState.FAILED.name)
        }
    }
}