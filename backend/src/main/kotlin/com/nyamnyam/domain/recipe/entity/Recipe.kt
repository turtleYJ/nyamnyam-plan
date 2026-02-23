package com.nyamnyam.domain.recipe.entity

import jakarta.persistence.*

@Entity
@Table(name = "recipes")
class Recipe(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false)
    val minMonth: Int,

    @Column(nullable = false)
    val maxMonth: Int,

    @Column(nullable = false)
    val cookTime: Int,

    @Column(columnDefinition = "TEXT")
    val instructions: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val category: RecipeCategory,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val stage: RecipeStage,

    @OneToMany(mappedBy = "recipe", cascade = [CascadeType.ALL], orphanRemoval = true)
    val ingredients: MutableList<RecipeIngredient> = mutableListOf(),

    @OneToOne(mappedBy = "recipe", cascade = [CascadeType.ALL], orphanRemoval = true)
    var nutrition: RecipeNutrition? = null
)

enum class RecipeCategory {
    BABY_FOOD, TODDLER_FOOD
}

enum class RecipeStage {
    EARLY, MIDDLE, LATE, COMPLETE
}
