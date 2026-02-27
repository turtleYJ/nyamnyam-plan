package com.nyamnyam.domain.recipe.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "ingredient_nutritions")
class IngredientNutrition(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false, unique = true)
    val ingredient: Ingredient,

    @Column(length = 20)
    val foodCode: String? = null,

    @Column(length = 100)
    val foodNameOfficial: String? = null,

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
    val zinc: BigDecimal = BigDecimal.ZERO,

    @Column(length = 50)
    val source: String? = null
)
