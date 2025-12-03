package com.example.codechecker.data.model

data class UserUpdateRequest(
    val displayName: String? = null
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)