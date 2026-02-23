package com.nyamnyam.domain.child.dto

import com.nyamnyam.domain.child.entity.Gender
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class ChildCreateRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 30, message = "Name must be 30 characters or less")
    val name: String,

    @field:NotNull(message = "Birth date is required")
    val birthDate: LocalDate,

    val gender: Gender? = null,

    val allergies: List<String> = emptyList()
)
