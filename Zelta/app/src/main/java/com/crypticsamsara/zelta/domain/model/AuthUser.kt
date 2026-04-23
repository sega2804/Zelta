package com.crypticsamsara.zelta.domain.model

data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
)
