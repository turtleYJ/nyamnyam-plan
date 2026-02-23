package com.nyamnyam.domain.child.entity

import com.nyamnyam.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "children")
class Child(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false, length = 30)
    var name: String,

    @Column(nullable = false)
    val birthDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    val gender: Gender? = null,

    @Column(columnDefinition = "JSON")
    var allergies: String = "[]",

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class Gender {
    MALE, FEMALE
}
