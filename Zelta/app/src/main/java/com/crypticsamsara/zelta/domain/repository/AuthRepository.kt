package com.crypticsamsara.zelta.domain.repository

import com.crypticsamsara.zelta.domain.model.AuthUser
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    // for current signed-in user and null if not signed in
    fun getCurrentUser(): Flow<AuthUser?>

    // google Sign-in
    suspend fun signInWithGoogle(idToken: String): ZeltaResult<AuthUser>

    // Email Sign-in
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): ZeltaResult<AuthUser>

    // Email registration
    suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String,
    ): ZeltaResult<AuthUser>


    // sign-out
    suspend fun signOut(): ZeltaResult<Unit>

    // to know if user is signed in
    fun isSignedIn(): Boolean



}