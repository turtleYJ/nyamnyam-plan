package com.nyamnyam.domain.plan.entity

import com.nyamnyam.domain.child.entity.Child
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "weekly_plans")
class WeeklyPlan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    val child: Child,

    @Column(nullable = false)
    val weekStartDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val createdBy: PlanCreator = PlanCreator.AI,

    @OneToMany(mappedBy = "plan", cascade = [CascadeType.ALL], orphanRemoval = true)
    val meals: MutableList<DailyMeal> = mutableListOf(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class PlanCreator {
    USER, AI
}
