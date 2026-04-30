package com.crypticsamsara.zelta.ui.auth

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypticsamsara.zelta.data.remote.SyncManager
import com.crypticsamsara.zelta.domain.model.ZeltaResult
import com.crypticsamsara.zelta.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager
): ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeAuthState()
    }



    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _uiState.update { it.copy (currentUser = user) }
            }
        }
    }

    fun onTabSelected(tab: AuthTab) {
        _uiState.update { it.copy(authTab = tab, errorMessage = null) }
    }

    // Google Sign-In
    fun onGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = authRepository.signInWithGoogle(idToken)) {
                is ZeltaResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentUser = result.data
                        )
                    }
                    // fetch and merge cloud data
                    viewModelScope.launch {
                        syncManager.fetchAndMerge(result.data.uid)
                    }
                }
                is ZeltaResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Google Sign-In failed"
                        )
                    }
                }
                else ->  Unit
            }
        }
    }


    // Email sign-in
    @SuppressLint("SuspiciousIndentation")
    fun onEmailSignIn(email: String, password: String) {
        if (!validateEmailSignIn(email, password)) return

            viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = authRepository.signInWithEmail(email, password)) {
                is ZeltaResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentUser = result.data
                        )
                    }
                }
                is ZeltaResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Invalid email or password"
                        )
                    }
                }
                else ->  Unit
            }
        }
    }

    // email registration
    fun onRegister(
        name: String,
        email: String,
        password: String
    ) {
        if (!validateRegistration(name, email, password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = authRepository.registerWithEmail(
                email = email,
                password = password,
                displayName = name
            )) {
                is ZeltaResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentUser = result.data
                        )
                    }
                }
                is ZeltaResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Registration failed"
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    // Sign out
    fun onSignOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Validation
    private fun validateEmailSignIn(
        email: String,
        password: String
    ): Boolean {
        Log.d("AuthDebug", "SignIn - email: '$email' password length: ${password.length}")

        if (email.isBlank() || !email.contains("@")) {
            _uiState.update { it.copy(errorMessage = "Enter a valid email") }
            return false
        }
        if (password.length < 8) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 8 characters") }
            return false
        }
        return true
    }

    private fun validateRegistration(
        name: String,
        email: String,
        password: String,
    ): Boolean {
        Log.d("AuthDebug", "Register — name: '$name' email: '$email' password length: ${password.length}")

        if(name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter your name") }
            return false
        }
        if (email.isBlank() || !email.contains("@")) {
            _uiState.update { it.copy(errorMessage = "Enter a valid email") }
            return false
        }
        if (password.length< 8) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 8 characters") }
            return false
            }
        return true
    }
}