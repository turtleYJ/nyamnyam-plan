package com.nyamnyam.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.channel.ChannelOption
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class WebClientConfig(
    private val aiServiceProperties: AiServiceProperties,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder().build()
    }

    @Bean
    @Qualifier("aiWebClient")
    fun aiWebClient(): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, aiServiceProperties.timeout.toInt())
            .responseTimeout(Duration.ofMillis(aiServiceProperties.timeout))

        val strategies = ExchangeStrategies.builder()
            .codecs { it.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper)) }
            .codecs { it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper)) }
            .build()

        return WebClient.builder()
            .baseUrl(aiServiceProperties.url)
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies)
            .build()
    }
}
