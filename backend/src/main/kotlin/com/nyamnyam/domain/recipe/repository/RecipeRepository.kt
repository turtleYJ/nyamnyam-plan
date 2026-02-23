package com.nyamnyam.domain.recipe.repository

import com.nyamnyam.domain.recipe.entity.Recipe
import com.nyamnyam.domain.recipe.entity.RecipeCategory
import com.nyamnyam.domain.recipe.entity.RecipeStage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RecipeRepository : JpaRepository<Recipe, Long> {

    @Query("""
        SELECT DISTINCT r FROM Recipe r
        LEFT JOIN FETCH r.ingredients ri
        LEFT JOIN FETCH ri.ingredient
        WHERE (:month IS NULL OR (r.minMonth <= :month AND r.maxMonth >= :month))
        AND (:category IS NULL OR r.category = :category)
        AND (:stage IS NULL OR r.stage = :stage)
    """)
    fun findFiltered(
        month: Int?,
        category: RecipeCategory?,
        stage: RecipeStage?
    ): List<Recipe>

    @Query("""
        SELECT DISTINCT r FROM Recipe r
        LEFT JOIN FETCH r.ingredients ri
        LEFT JOIN FETCH ri.ingredient
        LEFT JOIN FETCH r.nutrition
        WHERE r.id = :id
    """)
    fun findByIdWithDetails(id: Long): Recipe?
}
