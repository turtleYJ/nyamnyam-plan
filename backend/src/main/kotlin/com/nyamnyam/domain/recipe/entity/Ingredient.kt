package com.nyamnyam.domain.recipe.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "ingredients")
class Ingredient(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 50)
    val name: String,

    @Column(nullable = false, length = 20)
    val unit: String,

    @Column(precision = 10, scale = 2)
    val gramsPerUnit: BigDecimal? = null,

    @OneToOne(mappedBy = "ingredient", cascade = [CascadeType.ALL], orphanRemoval = true)
    var nutrition: IngredientNutrition? = null
)
