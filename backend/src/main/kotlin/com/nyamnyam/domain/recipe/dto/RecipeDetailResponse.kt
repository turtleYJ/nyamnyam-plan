package com.nyamnyam.domain.recipe.dto

import com.nyamnyam.domain.recipe.entity.Recipe
import com.nyamnyam.domain.recipe.entity.RecipeCategory
import com.nyamnyam.domain.recipe.entity.RecipeStage
import java.math.BigDecimal

data class RecipeDetailResponse(
    val id: Long,
    val name: String,
    val minMonth: Int,
    val maxMonth: Int,
    val cookTime: Int,
    val instructions: String,
    val category: RecipeCategory,
    val stage: RecipeStage,
    val ingredients: List<IngredientInfo>,
    val nutrition: NutritionInfo?
) {
    data class IngredientInfo(
        val name: String,
        val amount: BigDecimal,
        val unit: String
    )

    data class NutritionInfo(
        val calories: BigDecimal,
        val protein: BigDecimal,
        val iron: BigDecimal,
        val calcium: BigDecimal,
        val vitaminA: BigDecimal,
        val vitaminC: BigDecimal,
        val zinc: BigDecimal,
        val source: String?
    )

    companion object {
        fun from(recipe: Recipe): RecipeDetailResponse = RecipeDetailResponse(
            id = recipe.id,
            name = recipe.name,
            minMonth = recipe.minMonth,
            maxMonth = recipe.maxMonth,
            cookTime = recipe.cookTime,
            instructions = recipe.instructions,
            category = recipe.category,
            stage = recipe.stage,
            ingredients = recipe.ingredients.map { ri ->
                IngredientInfo(
                    name = ri.ingredient.name,
                    amount = ri.amount,
                    unit = ri.ingredient.unit
                )
            },
            nutrition = recipe.nutrition?.let { n ->
                NutritionInfo(
                    calories = n.calories,
                    protein = n.protein,
                    iron = n.iron,
                    calcium = n.calcium,
                    vitaminA = n.vitaminA,
                    vitaminC = n.vitaminC,
                    zinc = n.zinc,
                    source = n.source
                )
            }
        )
    }
}
