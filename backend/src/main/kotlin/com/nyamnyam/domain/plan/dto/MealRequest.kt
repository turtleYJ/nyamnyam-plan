package com.nyamnyam.domain.plan.dto

import com.nyamnyam.domain.plan.entity.MealTime
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class MealRequest(
    @field:NotNull(message = "Date is required")
    val date: LocalDate,

    @field:NotNull(message = "Meal time is required")
    val mealTime: MealTime,

    @field:NotNull(message = "Recipe ID is required")
    val recipeId: Long
)
