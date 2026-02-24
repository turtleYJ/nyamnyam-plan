package com.nyamnyam.infrastructure

import com.nyamnyam.domain.recipe.dto.RecipeResponse

data class AiRecipeInfo(
    val id: Long,
    val name: String,
    val category: String,
    val stage: String,
    val cookTime: Int,
    val ingredientNames: List<String>
) {
    companion object {
        fun from(recipe: RecipeResponse): AiRecipeInfo = AiRecipeInfo(
            id = recipe.id,
            name = recipe.name,
            category = recipe.category.name,
            stage = recipe.stage.name,
            cookTime = recipe.cookTime,
            ingredientNames = recipe.ingredientNames
        )
    }
}

data class AiMealPlanRequest(
    val childMonth: Int,
    val allergies: List<String>,
    val availableRecipes: List<AiRecipeInfo>,
    val days: Int = 7
)

data class AiMealSlot(
    val recipeId: Long,
    val recipeName: String
)

data class AiDayPlan(
    val day: Int,
    val breakfast: AiMealSlot,
    val lunch: AiMealSlot,
    val dinner: AiMealSlot,
    val snack: AiMealSlot
)

data class AiMealPlanResponse(
    val childMonth: Int,
    val days: Int,
    val meals: List<AiDayPlan>,
    val cached: Boolean = false
)
