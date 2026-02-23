package com.nyamnyam.domain.recipe.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "recipe_ingredients")
class RecipeIngredient(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    val recipe: Recipe,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    val ingredient: Ingredient,

    @Column(nullable = false, precision = 10, scale = 2)
    val amount: BigDecimal
)
