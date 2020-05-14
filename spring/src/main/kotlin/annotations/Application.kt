package de.e2.spring.annotations

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AdresseApplication

fun main(args: Array<String>) {
    runApplication<AdresseApplication>(*args) //#TOPIC Spreading
}