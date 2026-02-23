package com.nyamnyam.domain.recipe.dto

import com.nyamnyam.domain.recipe.entity.Recipe
import com.nyamnyam.domain.recipe.entity.RecipeCategory
import com.nyamnyam.domain.recipe.entity.RecipeStage

data class RecipeResponse(
    val id: Long,
    val name: String,
    val minMonth: Int,
    val maxMonth: Int,
    val cookTime: Int,
    val category: RecipeCategory,
    val stage: RecipeStage,
    val ingredientNames: List<String>
) {
    companion object {
        fun from(recipe: Recipe): RecipeResponse = RecipeResponse(
            id = recipe.id,
            name = recipe.name,
            minMonth = recipe.minMonth,
            maxMonth = recipe.maxMonth,
            cookTime = recipe.cookTime,
            category = recipe.category,
            stage = recipe.stage,
            ingredientNames = recipe.ingredients.map { it.ingredient.name }
        )
    }
}
