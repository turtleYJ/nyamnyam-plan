package com.nyamnyam.config

import io.netty.channel.ChannelOption
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class WebClientConfig(
    private val aiServiceProperties: AiServiceProperties
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

        return WebClient.builder()
            .baseUrl(aiServiceProperties.url)
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }
}
