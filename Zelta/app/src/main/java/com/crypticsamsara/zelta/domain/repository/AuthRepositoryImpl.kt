package com.crypticsamsara.zelta.domain.repository



import com.crypticsamsara.zelta.domain.model.AuthUser
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.model.safeCall
import com.crypticsamsara.zelta.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun getCurrentUser(): Flow<AuthUser?> = callbackFlow {
        val listener = AuthStateListener { auth ->
            trySend(auth.currentUser?.toAuthUser())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithGoogle(
        idToken: String
    ): ZeltaResult<AuthUser> = safeCall {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result     = firebaseAuth.signInWithCredential(credential).await()
        result.user?.toAuthUser()
            ?: throw Exception("Sign in failed — no user returned")
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): ZeltaResult<AuthUser> = safeCall {
        val result = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()
        result.user?.toAuthUser()
            ?: throw Exception("Sign in failed — no user returned")
    }

    override suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): ZeltaResult<AuthUser> = safeCall {
        val result = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()

        // Set display name after registration
        result.user?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
        )?.await()

        result.user?.toAuthUser()
            ?: throw Exception("Registration failed — no user returned")
    }

    override suspend fun signOut(): ZeltaResult<Unit> = safeCall {
        firebaseAuth.signOut()
    }

    override fun isSignedIn(): Boolean =
        firebaseAuth.currentUser != null

    //Extension
    private fun com.google.firebase.auth.FirebaseUser.toAuthUser() = AuthUser(
        uid         = uid,
        displayName = displayName ?: email?.substringBefore("@") ?: "User",
        email       = email ?: "",
        photoUrl    = photoUrl?.toString()
    )
}