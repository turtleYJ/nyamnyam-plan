package com.nyamnyam.domain.child.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nyamnyam.common.exception.BusinessException
import com.nyamnyam.common.exception.ErrorCode
import com.nyamnyam.domain.child.dto.ChildCreateRequest
import com.nyamnyam.domain.child.dto.ChildResponse
import com.nyamnyam.domain.child.dto.ChildUpdateRequest
import com.nyamnyam.domain.child.entity.Child
import com.nyamnyam.domain.child.repository.ChildRepository
import com.nyamnyam.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChildService(
    private val childRepository: ChildRepository,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper
) {
    companion object {
        private const val MAX_CHILDREN_PER_USER = 5
    }

    fun getChildren(userId: Long): List<ChildResponse> {
        return childRepository.findAllByUserIdOrderByCreatedAtAsc(userId)
            .map { ChildResponse.from(it) }
    }

    @Transactional
    fun createChild(userId: Long, request: ChildCreateRequest): ChildResponse {
        val count = childRepository.countByUserId(userId)
        if (count >= MAX_CHILDREN_PER_USER) {
            throw BusinessException(ErrorCode.CHILD_LIMIT_EXCEEDED)
        }

        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }

        val child = Child(
            user = user,
            name = request.name,
            birthDate = request.birthDate,
            gender = request.gender,
            allergies = objectMapper.writeValueAsString(request.allergies)
        )

        return ChildResponse.from(childRepository.save(child))
    }

    @Transactional
    fun updateChild(userId: Long, childId: Long, request: ChildUpdateRequest): ChildResponse {
        val child = childRepository.findByIdAndUserId(childId, userId)
            ?: throw BusinessException(ErrorCode.CHILD_NOT_FOUND)

        request.name?.let { child.name = it }
        request.allergies?.let { child.allergies = objectMapper.writeValueAsString(it) }

        return ChildResponse.from(child)
    }

    @Transactional
    fun deleteChild(userId: Long, childId: Long) {
        val child = childRepository.findByIdAndUserId(childId, userId)
            ?: throw BusinessException(ErrorCode.CHILD_NOT_FOUND)

        childRepository.delete(child)
    }
}
