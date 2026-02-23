package com.nyamnyam.domain.recipe.repository

import com.nyamnyam.domain.recipe.entity.Ingredient
import org.springframework.data.jpa.repository.JpaRepository

interface IngredientRepository : JpaRepository<Ingredient, Long> {
    fun findByName(name: String): Ingredient?
}
