package com.nyamnyam.auth.controller

import com.nyamnyam.auth.dto.LoginRequest
import com.nyamnyam.auth.dto.RefreshRequest
import com.nyamnyam.auth.dto.TokenResponse
import com.nyamnyam.auth.service.AuthService
import com.nyamnyam.common.security.CurrentUserId
import com.nyamnyam.domain.user.entity.AuthProvider
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login/kakao")
    fun loginWithKakao(@Valid @RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.login(AuthProvider.KAKAO, request))
    }

    @PostMapping("/login/naver")
    fun loginWithNaver(@Valid @RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.login(AuthProvider.NAVER, request))
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshRequest): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.refresh(request))
    }

    @PostMapping("/logout")
    fun logout(@CurrentUserId userId: Long): ResponseEntity<Unit> {
        authService.logout(userId)
        return ResponseEntity.noContent().build()
    }
}
