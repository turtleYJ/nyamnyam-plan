package com.nyamnyam.domain.recipe.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "recipe_nutritions")
class RecipeNutrition(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false, unique = true)
    val recipe: Recipe,

    @Column(precision = 8, scale = 2)
    val calories: BigDecimal = BigDecimal.ZERO,

    @Column(precision = 8, scale = 2)
    val protein: BigDecimal = BigDecimal.ZERO,

    @Column(precision = 8, scale = 2)
    val iron: BigDecimal = BigDecimal.ZERO,

    @Column(precision = 8, scale = 2)
    val calcium: BigDecimal = BigDecimal.ZERO,

    @Column(precision = 8, scale = 2)
    val vitaminA: BigDecimal = BigDecimal.ZERO,

    @Column(precision = 8, scale = 2)
    val vitaminC: BigDecimal = BigDecimal.ZERO,

    @Column(precision = 8, scale = 2)
    val zinc: BigDecimal = BigDecimal.ZERO
)
