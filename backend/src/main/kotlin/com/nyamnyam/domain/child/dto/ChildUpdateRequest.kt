package com.nyamnyam.domain.child.dto

import jakarta.validation.constraints.Size

data class ChildUpdateRequest(
    @field:Size(max = 30, message = "Name must be 30 characters or less")
    val name: String? = null,

    val allergies: List<String>? = null
)
