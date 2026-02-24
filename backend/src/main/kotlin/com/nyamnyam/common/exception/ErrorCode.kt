package com.nyamnyam.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    // Auth
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "Invalid or expired token"),
    OAUTH_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_002", "OAuth authentication failed"),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "AUTH_003", "Unsupported OAuth provider"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "User not found"),

    // Child
    CHILD_NOT_FOUND(HttpStatus.NOT_FOUND, "CHILD_001", "Child not found"),
    CHILD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHILD_002", "Access denied to this child profile"),
    CHILD_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "CHILD_003", "Maximum number of children reached"),

    // Recipe
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "RECIPE_001", "Recipe not found"),

    // Plan
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN_001", "Weekly plan not found"),
    PLAN_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PLAN_002", "Access denied to this weekly plan"),
    PLAN_ALREADY_EXISTS(HttpStatus.CONFLICT, "PLAN_003", "Weekly plan already exists for this week"),

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_001", "Invalid input"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_999", "Internal server error")
}
