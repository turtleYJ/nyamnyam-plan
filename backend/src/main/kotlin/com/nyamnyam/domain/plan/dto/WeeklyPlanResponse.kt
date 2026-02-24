package com.nyamnyam.domain.plan.dto

import com.nyamnyam.domain.plan.entity.DailyMeal
import com.nyamnyam.domain.plan.entity.MealTime
import com.nyamnyam.domain.plan.entity.PlanCreator
import com.nyamnyam.domain.plan.entity.WeeklyPlan
import java.time.LocalDate
import java.time.LocalDateTime

data class WeeklyPlanResponse(
    val id: Long,
    val childId: Long,
    val childName: String,
    val weekStartDate: LocalDate,
    val createdBy: PlanCreator,
    val meals: List<MealResponse>,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(plan: WeeklyPlan): WeeklyPlanResponse {
            return WeeklyPlanResponse(
                id = plan.id,
                childId = plan.child.id,
                childName = plan.child.name,
                weekStartDate = plan.weekStartDate,
                createdBy = plan.createdBy,
                meals = plan.meals.map { MealResponse.from(it) },
                createdAt = plan.createdAt
            )
        }
    }
}

data class MealResponse(
    val id: Long,
    val date: LocalDate,
    val mealTime: MealTime,
    val recipeId: Long,
    val recipeName: String
) {
    companion object {
        fun from(meal: DailyMeal): MealResponse {
            return MealResponse(
                id = meal.id,
                date = meal.date,
                mealTime = meal.mealTime,
                recipeId = meal.recipe.id,
                recipeName = meal.recipe.name
            )
        }
    }
}
