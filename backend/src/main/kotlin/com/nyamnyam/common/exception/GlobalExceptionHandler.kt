package com.nyamnyam.common.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            code = e.errorCode.code,
            message = e.errorCode.message
        )
        return ResponseEntity.status(e.errorCode.status).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val fieldErrors = e.bindingResult.fieldErrors.map { error ->
            val snakeField = error.field.replace(Regex("([a-z])([A-Z])")) {
                "${it.groupValues[1]}_${it.groupValues[2].lowercase()}"
            }
            FieldError(field = snakeField, message = error.defaultMessage ?: "Invalid value")
        }
        val response = ErrorResponse(
            code = ErrorCode.INVALID_INPUT.code,
            message = ErrorCode.INVALID_INPUT.message,
            errors = fieldErrors
        )
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            code = ErrorCode.INTERNAL_ERROR.code,
            message = ErrorCode.INTERNAL_ERROR.message
        )
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.status).body(response)
    }
}
