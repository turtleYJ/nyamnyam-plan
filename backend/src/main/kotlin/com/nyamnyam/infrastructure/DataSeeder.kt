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
import java.math.RoundingMode

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

        val nutritionMap = loadIngredientNutrition()
        log.info("Loaded {} ingredient nutrition entries", nutritionMap.size)

        val ingredientCache = mutableMapOf<String, Ingredient>()

        log.info("Loading recipe seed data...")
        val json = ClassPathResource("data/recipes.json").inputStream.bufferedReader().readText()
        val seedRecipes: List<SeedRecipe> = objectMapper.readValue(json, object : TypeReference<List<SeedRecipe>>() {})

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
                    val nutData = nutritionMap[si.name]
                    val gramsPerUnit = nutData?.gramsPerUnit?.let { BigDecimal(it.toString()) }

                    val saved = ingredientRepository.findByName(si.name)
                        ?: ingredientRepository.save(
                            Ingredient(name = si.name, unit = si.unit, gramsPerUnit = gramsPerUnit)
                        )

                    if (nutData != null && saved.nutrition == null) {
                        val p = nutData.per100g
                        saved.nutrition = IngredientNutrition(
                            ingredient = saved,
                            foodCode = nutData.foodCode,
                            foodNameOfficial = nutData.foodNameOfficial,
                            calories = bd(p.calories),
                            protein = bd(p.protein),
                            iron = bd(p.iron),
                            calcium = bd(p.calcium),
                            vitaminA = bd(p.vitaminA),
                            vitaminC = bd(p.vitaminC),
                            zinc = bd(p.zinc),
                            source = nutData.source
                        )
                        ingredientRepository.save(saved)
                    }

                    saved
                }
                recipe.ingredients.add(
                    RecipeIngredient(
                        recipe = recipe,
                        ingredient = ingredient,
                        amount = BigDecimal(si.amount.toString())
                    )
                )
            }

            recipe.nutrition = calculateNutrition(recipe, nutritionMap)
            recipeRepository.save(recipe)
        }

        log.info("Loaded {} recipes with seed data", seedRecipes.size)
    }

    private fun loadIngredientNutrition(): Map<String, SeedIngredientNutrition> {
        val json = ClassPathResource("data/ingredient-nutrition.json").inputStream.bufferedReader().readText()
        val list: List<SeedIngredientNutrition> = objectMapper.readValue(
            json, object : TypeReference<List<SeedIngredientNutrition>>() {}
        )
        return list.associateBy { it.name }
    }

    private fun calculateNutrition(recipe: Recipe, nutritionMap: Map<String, SeedIngredientNutrition>): RecipeNutrition {
        var totalCalories = BigDecimal.ZERO
        var totalProtein = BigDecimal.ZERO
        var totalIron = BigDecimal.ZERO
        var totalCalcium = BigDecimal.ZERO
        var totalVitaminA = BigDecimal.ZERO
        var totalVitaminC = BigDecimal.ZERO
        var totalZinc = BigDecimal.ZERO

        for (ri in recipe.ingredients) {
            val nutData = nutritionMap[ri.ingredient.name] ?: continue
            val p = nutData.per100g

            // amount를 g으로 환산
            val gpu = nutData.gramsPerUnit
            val amountInGrams = when {
                ri.ingredient.unit == "개" && gpu != null -> ri.amount * bd(gpu)
                ri.ingredient.unit == "ml" && gpu != null -> ri.amount * bd(gpu)
                else -> ri.amount
            }

            val ratio = amountInGrams.divide(BigDecimal(100), 10, RoundingMode.HALF_UP)

            totalCalories = totalCalories.add(ratio.multiply(bd(p.calories)))
            totalProtein = totalProtein.add(ratio.multiply(bd(p.protein)))
            totalIron = totalIron.add(ratio.multiply(bd(p.iron)))
            totalCalcium = totalCalcium.add(ratio.multiply(bd(p.calcium)))
            totalVitaminA = totalVitaminA.add(ratio.multiply(bd(p.vitaminA)))
            totalVitaminC = totalVitaminC.add(ratio.multiply(bd(p.vitaminC)))
            totalZinc = totalZinc.add(ratio.multiply(bd(p.zinc)))
        }

        return RecipeNutrition(
            recipe = recipe,
            calories = totalCalories.setScale(2, RoundingMode.HALF_UP),
            protein = totalProtein.setScale(2, RoundingMode.HALF_UP),
            iron = totalIron.setScale(2, RoundingMode.HALF_UP),
            calcium = totalCalcium.setScale(2, RoundingMode.HALF_UP),
            vitaminA = totalVitaminA.setScale(2, RoundingMode.HALF_UP),
            vitaminC = totalVitaminC.setScale(2, RoundingMode.HALF_UP),
            zinc = totalZinc.setScale(2, RoundingMode.HALF_UP)
        )
    }

    private fun bd(value: Double): BigDecimal = BigDecimal(value.toString())

    data class SeedRecipe(
        val name: String,
        @JsonProperty("minMonth") val minMonth: Int,
        @JsonProperty("maxMonth") val maxMonth: Int,
        @JsonProperty("cookTime") val cookTime: Int,
        val instructions: String,
        val category: String,
        val stage: String,
        val ingredients: List<SeedIngredient>
    )

    data class SeedIngredient(
        val name: String,
        val unit: String,
        val amount: Double
    )

    data class SeedIngredientNutrition(
        val name: String,
        val unit: String,
        val gramsPerUnit: Double?,
        val foodCode: String?,
        val foodNameOfficial: String?,
        val per100g: NutritionValues,
        val source: String?
    )

    data class NutritionValues(
        val calories: Double,
        val protein: Double,
        val iron: Double,
        val calcium: Double,
        @JsonProperty("vitaminA") val vitaminA: Double,
        @JsonProperty("vitaminC") val vitaminC: Double,
        val zinc: Double
    )
}
