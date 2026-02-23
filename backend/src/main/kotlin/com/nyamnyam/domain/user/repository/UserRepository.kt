package com.nyamnyam.domain.user.repository

import com.nyamnyam.domain.user.entity.AuthProvider
import com.nyamnyam.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByProviderAndProviderId(provider: AuthProvider, providerId: String): User?
    fun findByEmail(email: String): User?
}
