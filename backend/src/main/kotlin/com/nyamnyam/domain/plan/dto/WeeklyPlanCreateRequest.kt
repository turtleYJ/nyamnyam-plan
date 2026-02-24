package com.nyamnyam.domain.plan.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class WeeklyPlanCreateRequest(
    @field:NotNull(message = "Child ID is required")
    val childId: Long,

    @field:NotNull(message = "Week start date is required")
    val weekStartDate: LocalDate,

    @field:NotEmpty(message = "At least one meal is required")
    @field:Valid
    val meals: List<MealRequest>
)
