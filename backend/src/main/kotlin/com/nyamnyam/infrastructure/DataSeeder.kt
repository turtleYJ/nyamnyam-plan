package com.nyamnyam.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.nyamnyam.domain.recipe.entity.*
import com.nyamnyam.domain.recipe.repository.IngredientRepository
import com.nyamnyam.domain.recipe.repository.RecipeRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Component
class DataSeeder(
    private val recipeRepository: RecipeRepository,
    private val ingredientRepository: IngredientRepository,
    private val objectMapper: ObjectMapper
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(args: ApplicationArguments?) {
        if (recipeRepository.count() > 0) {
            log.info("Recipes already exist, skipping seed data loading")
            return
        }

        log.info("Loading recipe seed data...")
        val json = ClassPathResource("data/recipes.json").inputStream.bufferedReader().readText()
        val seedRecipes: List<SeedRecipe> = objectMapper.readValue(json, object : TypeReference<List<SeedRecipe>>() {})

        val ingredientCache = mutableMapOf<String, Ingredient>()

        for (seed in seedRecipes) {
            val recipe = Recipe(
                name = seed.name,
                minMonth = seed.minMonth,
                maxMonth = seed.maxMonth,
                cookTime = seed.cookTime,
                instructions = seed.instructions,
                category = RecipeCategory.valueOf(seed.category),
                stage = RecipeStage.valueOf(seed.stage)
            )

            for (si in seed.ingredients) {
                val ingredient = ingredientCache.getOrPut(si.name) {
                    ingredientRepository.findByName(si.name)
                        ?: ingredientRepository.save(Ingredient(name = si.name, unit = si.unit))
                }
                recipe.ingredients.add(
                    RecipeIngredient(
                        recipe = recipe,
                        ingredient = ingredient,
                        amount = BigDecimal(si.amount.toString())
                    )
                )
            }

            seed.nutrition?.let { n ->
                recipe.nutrition = RecipeNutrition(
                    recipe = recipe,
                    calories = BigDecimal(n.calories.toString()),
                    protein = BigDecimal(n.protein.toString()),
                    iron = BigDecimal(n.iron.toString()),
                    calcium = BigDecimal(n.calcium.toString()),
                    vitaminA = BigDecimal(n.vitaminA.toString()),
                    vitaminC = BigDecimal(n.vitaminC.toString()),
                    zinc = BigDecimal(n.zinc.toString())
                )
            }

            recipeRepository.save(recipe)
        }

        log.info("Loaded {} recipes with seed data", seedRecipes.size)
    }

    data class SeedRecipe(
        val name: String,
        @JsonProperty("minMonth") val minMonth: Int,
        @JsonProperty("maxMonth") val maxMonth: Int,
        @JsonProperty("cookTime") val cookTime: Int,
        val instructions: String,
        val category: String,
        val stage: String,
        val ingredients: List<SeedIngredient>,
        val nutrition: SeedNutrition?
    )

    data class SeedIngredient(
        val name: String,
        val unit: String,
        val amount: Double
    )

    data class SeedNutrition(
        val calories: Double,
        val protein: Double,
        val iron: Double,
        val calcium: Double,
        @JsonProperty("vitaminA") val vitaminA: Double,
        @JsonProperty("vitaminC") val vitaminC: Double,
        val zinc: Double
    )
}
