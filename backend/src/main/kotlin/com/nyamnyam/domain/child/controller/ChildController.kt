package com.nyamnyam.domain.child.controller

import com.nyamnyam.common.security.CurrentUserId
import com.nyamnyam.domain.child.dto.ChildCreateRequest
import com.nyamnyam.domain.child.dto.ChildResponse
import com.nyamnyam.domain.child.dto.ChildUpdateRequest
import com.nyamnyam.domain.child.service.ChildService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/children")
class ChildController(
    private val childService: ChildService
) {

    @GetMapping
    fun getChildren(@CurrentUserId userId: Long): ResponseEntity<List<ChildResponse>> {
        return ResponseEntity.ok(childService.getChildren(userId))
    }

    @PostMapping
    fun createChild(
        @CurrentUserId userId: Long,
        @Valid @RequestBody request: ChildCreateRequest
    ): ResponseEntity<ChildResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(childService.createChild(userId, request))
    }

    @PutMapping("/{id}")
    fun updateChild(
        @CurrentUserId userId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: ChildUpdateRequest
    ): ResponseEntity<ChildResponse> {
        return ResponseEntity.ok(childService.updateChild(userId, id, request))
    }

    @DeleteMapping("/{id}")
    fun deleteChild(
        @CurrentUserId userId: Long,
        @PathVariable id: Long
    ): ResponseEntity<Unit> {
        childService.deleteChild(userId, id)
        return ResponseEntity.noContent().build()
    }
}
