package com.nyamnyam.auth.jwt

import com.nyamnyam.config.JwtProperties
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties,
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
    }

    fun generateAccessToken(userId: Long, email: String): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("type", "access")
            .issuedAt(now)
            .expiration(Date(now.time + jwtProperties.accessTokenExpiry))
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(userId: Long): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(Date(now.time + jwtProperties.refreshTokenExpiry))
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        } catch (e: Exception) {
            when (e) {
                is SecurityException, is MalformedJwtException,
                is ExpiredJwtException, is IllegalArgumentException -> false
                else -> false
            }
        }
    }

    fun getUserIdFromToken(token: String): Long {
        val claims = Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).payload
        return claims.subject.toLong()
    }

    fun saveRefreshToken(userId: Long, refreshToken: String) {
        val key = "refresh_token:$userId"
        redisTemplate.opsForValue().set(
            key, refreshToken, Duration.ofMillis(jwtProperties.refreshTokenExpiry)
        )
    }

    fun getRefreshToken(userId: Long): String? {
        return redisTemplate.opsForValue().get("refresh_token:$userId")
    }

    fun deleteRefreshToken(userId: Long) {
        redisTemplate.delete("refresh_token:$userId")
    }
}
