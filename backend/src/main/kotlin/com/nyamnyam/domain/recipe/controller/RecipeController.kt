package com.nyamnyam.domain.recipe.controller

import com.nyamnyam.domain.recipe.dto.RecipeDetailResponse
import com.nyamnyam.domain.recipe.dto.RecipeResponse
import com.nyamnyam.domain.recipe.entity.RecipeCategory
import com.nyamnyam.domain.recipe.entity.RecipeStage
import com.nyamnyam.domain.recipe.service.RecipeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recipes")
class RecipeController(
    private val recipeService: RecipeService
) {

    @GetMapping
    fun getRecipes(
        @RequestParam(required = false) month: Int?,
        @RequestParam(required = false) category: RecipeCategory?,
        @RequestParam(required = false) stage: RecipeStage?,
        @RequestParam(required = false) exclude: List<String>?
    ): ResponseEntity<List<RecipeResponse>> {
        return ResponseEntity.ok(recipeService.getRecipes(month, category, stage, exclude))
    }

    @GetMapping("/{id}")
    fun getRecipeDetail(@PathVariable id: Long): ResponseEntity<RecipeDetailResponse> {
        return ResponseEntity.ok(recipeService.getRecipeDetail(id))
    }
}
