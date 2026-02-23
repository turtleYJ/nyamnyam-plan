package com.nyamnyam.domain.user.service

import com.nyamnyam.common.exception.BusinessException
import com.nyamnyam.common.exception.ErrorCode
import com.nyamnyam.domain.user.dto.UserResponse
import com.nyamnyam.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository
) {

    fun getUser(userId: Long): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }
        return UserResponse.from(user)
    }
}
