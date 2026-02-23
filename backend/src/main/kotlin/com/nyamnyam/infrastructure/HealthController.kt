package com.nyamnyam.infrastructure

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class HealthController {

    @GetMapping("/api/health")
    fun health(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(
            mapOf(
                "status" to "UP",
                "service" to "nyamnyam-backend",
                "timestamp" to LocalDateTime.now().toString()
            )
        )
    }
}
