package com.nyamnyam.domain.plan.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.nyamnyam.common.exception.BusinessException
import com.nyamnyam.common.exception.ErrorCode
import com.nyamnyam.domain.child.repository.ChildRepository
import com.nyamnyam.domain.plan.dto.GeneratePlanRequest
import com.nyamnyam.domain.plan.dto.MealRequest
import com.nyamnyam.domain.plan.dto.WeeklyPlanCreateRequest
import com.nyamnyam.domain.plan.dto.WeeklyPlanResponse
import com.nyamnyam.domain.plan.dto.WeeklyPlanUpdateRequest
import com.nyamnyam.domain.plan.entity.DailyMeal
import com.nyamnyam.domain.plan.entity.MealTime
import com.nyamnyam.domain.plan.entity.PlanCreator
import com.nyamnyam.domain.plan.entity.WeeklyPlan
import com.nyamnyam.domain.plan.repository.WeeklyPlanRepository
import com.nyamnyam.domain.recipe.repository.RecipeRepository
import com.nyamnyam.domain.recipe.service.RecipeService
import com.nyamnyam.infrastructure.AiMealPlanClient
import com.nyamnyam.infrastructure.AiMealPlanRequest
import com.nyamnyam.infrastructure.AiRecipeInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.Period

@Service
@Transactional(readOnly = true)
class WeeklyPlanService(
    private val weeklyPlanRepository: WeeklyPlanRepository,
    private val childRepository: ChildRepository,
    private val recipeRepository: RecipeRepository,
    private val recipeService: RecipeService,
    private val aiMealPlanClient: AiMealPlanClient,
    private val objectMapper: ObjectMapper
) {

    fun getPlans(userId: Long, childId: Long): List<WeeklyPlanResponse> {
        verifyChildOwnership(userId, childId)
        return weeklyPlanRepository.findAllByChildIdWithMeals(childId)
            .map { WeeklyPlanResponse.from(it) }
    }

    fun getPlan(userId: Long, planId: Long): WeeklyPlanResponse {
        val plan = findPlanOrThrow(planId)
        verifyPlanOwnership(userId, plan)
        return WeeklyPlanResponse.from(plan)
    }

    @Transactional
    fun createPlan(userId: Long, request: WeeklyPlanCreateRequest): WeeklyPlanResponse {
        val child = childRepository.findByIdAndUserId(request.childId, userId)
            ?: throw BusinessException(ErrorCode.CHILD_NOT_FOUND)

        if (weeklyPlanRepository.existsByChildIdAndWeekStartDate(request.childId, request.weekStartDate)) {
            throw BusinessException(ErrorCode.PLAN_ALREADY_EXISTS)
        }

        val plan = WeeklyPlan(
            child = child,
            weekStartDate = request.weekStartDate,
            createdBy = PlanCreator.USER
        )

        addMeals(plan, request.meals)

        val saved = weeklyPlanRepository.save(plan)
        return WeeklyPlanResponse.from(saved)
    }

    @Transactional
    fun generateAiPlan(userId: Long, request: GeneratePlanRequest): WeeklyPlanResponse {
        val child = childRepository.findByIdAndUserId(request.childId, userId)
            ?: throw BusinessException(ErrorCode.CHILD_NOT_FOUND)

        if (weeklyPlanRepository.existsByChildIdAndWeekStartDate(request.childId, request.weekStartDate)) {
            throw BusinessException(ErrorCode.PLAN_ALREADY_EXISTS)
        }

        val monthAge = Period.between(child.birthDate, LocalDate.now()).toTotalMonths().toInt()
        val allergies: List<String> = objectMapper.readValue(
            child.allergies,
            object : TypeReference<List<String>>() {}
        )

        val recipes = recipeService.getRecipes(
            month = monthAge,
            category = null,
            stage = null,
            exclude = allergies.ifEmpty { null }
        )

        if (recipes.size < 4) {
            throw BusinessException(ErrorCode.AI_INSUFFICIENT_RECIPES)
        }

        val aiRequest = AiMealPlanRequest(
            childMonth = monthAge,
            allergies = allergies,
            availableRecipes = recipes.map { AiRecipeInfo.from(it) }
        )

        val aiResponse = aiMealPlanClient.generateMealPlan(aiRequest)

        val plan = WeeklyPlan(
            child = child,
            weekStartDate = request.weekStartDate,
            createdBy = PlanCreator.AI
        )

        val mealRequests = aiResponse.meals.flatMap { dayPlan ->
            val date = request.weekStartDate.plusDays((dayPlan.day - 1).toLong())
            listOf(
                dayPlan.breakfast to MealTime.BREAKFAST,
                dayPlan.lunch to MealTime.LUNCH,
                dayPlan.dinner to MealTime.DINNER,
                dayPlan.snack to MealTime.SNACK
            ).map { (slot, mealTime) ->
                MealRequest(
                    date = date,
                    mealTime = mealTime,
                    recipeId = slot.recipeId
                )
            }
        }

        addMeals(plan, mealRequests)

        val saved = weeklyPlanRepository.save(plan)
        return WeeklyPlanResponse.from(saved)
    }

    @Transactional
    fun updatePlan(userId: Long, planId: Long, request: WeeklyPlanUpdateRequest): WeeklyPlanResponse {
        val plan = findPlanOrThrow(planId)
        verifyPlanOwnership(userId, plan)

        plan.meals.clear()
        addMeals(plan, request.meals)

        return WeeklyPlanResponse.from(plan)
    }

    @Transactional
    fun deletePlan(userId: Long, planId: Long) {
        val plan = findPlanOrThrow(planId)
        verifyPlanOwnership(userId, plan)
        weeklyPlanRepository.delete(plan)
    }

    private fun addMeals(plan: WeeklyPlan, mealRequests: List<MealRequest>) {
        mealRequests.forEach { req ->
            val recipe = recipeRepository.findById(req.recipeId)
                .orElseThrow { BusinessException(ErrorCode.RECIPE_NOT_FOUND) }

            plan.meals.add(
                DailyMeal(
                    plan = plan,
                    date = req.date,
                    mealTime = req.mealTime,
                    recipe = recipe
                )
            )
        }
    }

    private fun findPlanOrThrow(planId: Long): WeeklyPlan {
        return weeklyPlanRepository.findByIdWithDetails(planId)
            ?: throw BusinessException(ErrorCode.PLAN_NOT_FOUND)
    }

    private fun verifyChildOwnership(userId: Long, childId: Long) {
        childRepository.findByIdAndUserId(childId, userId)
            ?: throw BusinessException(ErrorCode.CHILD_ACCESS_DENIED)
    }

    private fun verifyPlanOwnership(userId: Long, plan: WeeklyPlan) {
        if (plan.child.user.id != userId) {
            throw BusinessException(ErrorCode.PLAN_ACCESS_DENIED)
        }
    }
}
