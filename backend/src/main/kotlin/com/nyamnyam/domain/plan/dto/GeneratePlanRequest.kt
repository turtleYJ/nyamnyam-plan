package com.nyamnyam.domain.plan.dto

import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class GeneratePlanRequest(
    @field:NotNull(message = "Child ID is required")
    val childId: Long,

    @field:NotNull(message = "Week start date is required")
    val weekStartDate: LocalDate
)
