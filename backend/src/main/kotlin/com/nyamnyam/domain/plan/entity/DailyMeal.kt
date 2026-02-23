package com.nyamnyam.domain.plan.entity

import com.nyamnyam.domain.recipe.entity.Recipe
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "daily_meals")
class DailyMeal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    val plan: WeeklyPlan,

    @Column(nullable = false)
    val date: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val mealTime: MealTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    val recipe: Recipe
)

enum class MealTime {
    BREAKFAST, LUNCH, DINNER, SNACK
}
