package com.nyamnyam.domain.plan.dto

import com.nyamnyam.domain.plan.entity.WeeklyPlan
import java.math.BigDecimal
import java.time.LocalDate

data class ShoppingListResponse(
    val planId: Long,
    val weekStartDate: LocalDate,
    val items: List<ShoppingItem>,
    val totalItems: Int
) {
    data class ShoppingItem(
        val ingredientName: String,
        val totalAmount: BigDecimal,
        val unit: String,
        val recipeNames: List<String>
    )

    companion object {
        private val EXCLUDED_INGREDIENTS = setOf("물")

        fun from(plan: WeeklyPlan): ShoppingListResponse {
            // 1. recipeId별 등장 횟수
            val recipeCount = plan.meals.groupBy { it.recipe.id }
                .mapValues { it.value.size }

            // 2. 각 recipe의 재료 × 등장 횟수 → 재료별 합산
            data class IngredientKey(val name: String, val unit: String)

            val aggregated = mutableMapOf<IngredientKey, Pair<BigDecimal, MutableSet<String>>>()

            recipeCount.forEach { (recipeId, count) ->
                val meal = plan.meals.first { it.recipe.id == recipeId }
                val recipe = meal.recipe

                recipe.ingredients.forEach { ri ->
                    val key = IngredientKey(ri.ingredient.name, ri.ingredient.unit)
                    val totalForThis = ri.amount.multiply(BigDecimal(count))
                    val existing = aggregated[key]
                    if (existing != null) {
                        aggregated[key] = Pair(
                            existing.first.add(totalForThis),
                            existing.second.apply { add(recipe.name) }
                        )
                    } else {
                        aggregated[key] = Pair(totalForThis, mutableSetOf(recipe.name))
                    }
                }
            }

            // 3. "물" 제외
            val items = aggregated
                .filter { it.key.name !in EXCLUDED_INGREDIENTS }
                .map { (key, value) ->
                    ShoppingItem(
                        ingredientName = key.name,
                        totalAmount = value.first,
                        unit = key.unit,
                        recipeNames = value.second.sorted()
                    )
                }
                .sortedBy { it.ingredientName }

            return ShoppingListResponse(
                planId = plan.id,
                weekStartDate = plan.weekStartDate,
                items = items,
                totalItems = items.size
            )
        }
    }
}
