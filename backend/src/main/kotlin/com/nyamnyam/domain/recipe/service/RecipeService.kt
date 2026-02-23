package com.nyamnyam.domain.recipe.service

import com.nyamnyam.common.exception.BusinessException
import com.nyamnyam.common.exception.ErrorCode
import com.nyamnyam.domain.recipe.dto.RecipeDetailResponse
import com.nyamnyam.domain.recipe.dto.RecipeResponse
import com.nyamnyam.domain.recipe.entity.RecipeCategory
import com.nyamnyam.domain.recipe.entity.RecipeStage
import com.nyamnyam.domain.recipe.repository.RecipeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RecipeService(
    private val recipeRepository: RecipeRepository
) {

    fun getRecipes(
        month: Int?,
        category: RecipeCategory?,
        stage: RecipeStage?,
        exclude: List<String>?
    ): List<RecipeResponse> {
        val recipes = recipeRepository.findFiltered(month, category, stage)

        val excludeSet = exclude?.toSet() ?: emptySet()
        if (excludeSet.isEmpty()) {
            return recipes.map { RecipeResponse.from(it) }
        }

        return recipes
            .filter { recipe ->
                recipe.ingredients.none { ri -> ri.ingredient.name in excludeSet }
            }
            .map { RecipeResponse.from(it) }
    }

    fun getRecipeDetail(id: Long): RecipeDetailResponse {
        val recipe = recipeRepository.findByIdWithDetails(id)
            ?: throw BusinessException(ErrorCode.RECIPE_NOT_FOUND)
        return RecipeDetailResponse.from(recipe)
    }
}
