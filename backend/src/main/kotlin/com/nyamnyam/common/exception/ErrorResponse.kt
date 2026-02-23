package com.nyamnyam.common.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val errors: List<FieldError>? = null
)

data class FieldError(
    val field: String,
    val message: String
)
