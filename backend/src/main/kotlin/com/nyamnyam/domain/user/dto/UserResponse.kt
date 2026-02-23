package com.nyamnyam.domain.user.dto

import com.nyamnyam.domain.user.entity.AuthProvider
import com.nyamnyam.domain.user.entity.User

data class UserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val provider: AuthProvider
) {
    companion object {
        fun from(user: User): UserResponse = UserResponse(
            id = user.id,
            email = user.email,
            nickname = user.nickname,
            provider = user.provider
        )
    }
}
