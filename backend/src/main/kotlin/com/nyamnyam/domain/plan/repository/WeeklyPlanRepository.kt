package com.nyamnyam.domain.plan.repository

import com.nyamnyam.domain.plan.entity.WeeklyPlan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface WeeklyPlanRepository : JpaRepository<WeeklyPlan, Long> {

    @Query("""
        SELECT DISTINCT p FROM WeeklyPlan p
        LEFT JOIN FETCH p.meals m
        LEFT JOIN FETCH m.recipe
        LEFT JOIN FETCH p.child
        WHERE p.child.id = :childId
        ORDER BY p.weekStartDate DESC
    """)
    fun findAllByChildIdWithMeals(childId: Long): List<WeeklyPlan>

    @Query("""
        SELECT DISTINCT p FROM WeeklyPlan p
        LEFT JOIN FETCH p.meals m
        LEFT JOIN FETCH m.recipe
        LEFT JOIN FETCH p.child
        WHERE p.id = :id
    """)
    fun findByIdWithDetails(id: Long): WeeklyPlan?
}
