package com.nyamnyam.domain.plan.controller

import com.nyamnyam.common.security.CurrentUserId
import com.nyamnyam.domain.plan.dto.WeeklyPlanCreateRequest
import com.nyamnyam.domain.plan.dto.WeeklyPlanResponse
import com.nyamnyam.domain.plan.service.WeeklyPlanService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/plans")
class WeeklyPlanController(
    private val weeklyPlanService: WeeklyPlanService
) {

    @GetMapping
    fun getPlans(
        @CurrentUserId userId: Long,
        @RequestParam childId: Long
    ): ResponseEntity<List<WeeklyPlanResponse>> {
        return ResponseEntity.ok(weeklyPlanService.getPlans(userId, childId))
    }

    @GetMapping("/{id}")
    fun getPlan(
        @CurrentUserId userId: Long,
        @PathVariable id: Long
    ): ResponseEntity<WeeklyPlanResponse> {
        return ResponseEntity.ok(weeklyPlanService.getPlan(userId, id))
    }

    @PostMapping
    fun createPlan(
        @CurrentUserId userId: Long,
        @Valid @RequestBody request: WeeklyPlanCreateRequest
    ): ResponseEntity<WeeklyPlanResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(weeklyPlanService.createPlan(userId, request))
    }

    @PutMapping("/{id}")
    fun updatePlan(
        @CurrentUserId userId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: WeeklyPlanCreateRequest
    ): ResponseEntity<WeeklyPlanResponse> {
        return ResponseEntity.ok(weeklyPlanService.updatePlan(userId, id, request))
    }

    @DeleteMapping("/{id}")
    fun deletePlan(
        @CurrentUserId userId: Long,
        @PathVariable id: Long
    ): ResponseEntity<Unit> {
        weeklyPlanService.deletePlan(userId, id)
        return ResponseEntity.noContent().build()
    }
}
