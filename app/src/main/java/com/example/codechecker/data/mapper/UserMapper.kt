package com.example.codechecker.data.mapper

import com.example.codechecker.data.local.entity.UserEntity
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserRole
import java.util.Date

fun User.toEntity(isCurrent: Boolean = false): UserEntity {
    return UserEntity(
        id = id,
        username = username,
        displayName = displayName,
        role = role.name,
        passwordHash = passwordHash,
        createdAt = Date(createdAt),
        lastLoginAt = lastLoginAt,
        isCurrent = isCurrent
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        username = username,
        displayName = displayName,
        passwordHash = passwordHash,
        role = UserRole.valueOf(role),
        createdAt = createdAt.time,
        lastLoginAt = lastLoginAt
    )
}