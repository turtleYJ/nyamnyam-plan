package com.nyamnyam

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class NyamnyamApplication

fun main(args: Array<String>) {
    runApplication<NyamnyamApplication>(*args)
}
