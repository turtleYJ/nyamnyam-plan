package com.nyamnyam.domain.child.dto

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.nyamnyam.domain.child.entity.Child
import com.nyamnyam.domain.child.entity.Gender
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

data class ChildResponse(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
    val gender: Gender?,
    val allergies: List<String>,
    val ageInMonths: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        private val objectMapper = ObjectMapper()

        fun from(child: Child): ChildResponse {
            val allergiesList: List<String> = if (child.allergies.isBlank() || child.allergies == "[]") {
                emptyList()
            } else {
                objectMapper.readValue(child.allergies, object : TypeReference<List<String>>() {})
            }

            return ChildResponse(
                id = child.id,
                name = child.name,
                birthDate = child.birthDate,
                gender = child.gender,
                allergies = allergiesList,
                ageInMonths = Period.between(child.birthDate, LocalDate.now()).toTotalMonths().toInt(),
                createdAt = child.createdAt
            )
        }
    }
}
