package com.nyamnyam.domain.child.repository

import com.nyamnyam.domain.child.entity.Child
import org.springframework.data.jpa.repository.JpaRepository

interface ChildRepository : JpaRepository<Child, Long> {
    fun findAllByUserIdOrderByCreatedAtAsc(userId: Long): List<Child>
    fun findByIdAndUserId(id: Long, userId: Long): Child?
    fun countByUserId(userId: Long): Int
}
