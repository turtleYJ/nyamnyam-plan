package com.nyamnyam.infrastructure

import com.nyamnyam.common.exception.BusinessException
import com.nyamnyam.common.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class AiMealPlanClient(
    @Qualifier("aiWebClient") private val aiWebClient: WebClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun generateMealPlan(request: AiMealPlanRequest): AiMealPlanResponse {
        try {
            return aiWebClient.post()
                .uri("/api/generate-meal-plan")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiMealPlanResponse::class.java)
                .block()
                ?: throw BusinessException(ErrorCode.AI_SERVICE_ERROR)
        } catch (e: BusinessException) {
            throw e
        } catch (e: WebClientResponseException) {
            log.error("AI service returned error: {} {}", e.statusCode, e.responseBodyAsString)
            throw BusinessException(ErrorCode.AI_SERVICE_ERROR)
        } catch (e: Exception) {
            log.error("AI service call failed: {}", e.message)
            throw BusinessException(ErrorCode.AI_SERVICE_ERROR)
        }
    }
}
