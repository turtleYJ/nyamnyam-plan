package com.nyamnyam.domain.plan.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty

data class WeeklyPlanUpdateRequest(
    @field:NotEmpty(message = "At least one meal is required")
    @field:Valid
    val meals: List<MealRequest>
)
