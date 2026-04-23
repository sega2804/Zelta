package com.crypticsamsara.zelta.ui.auth

import com.crypticsamsara.zelta.domain.model.AuthUser

data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: AuthUser? = null,
    val errorMessage: String? = null,
    val authTab: AuthTab = AuthTab.SIGN_IN
)

enum class AuthTab(val label: String) {
    SIGN_IN("Sign In"),
    REGISTER("Register")
}
