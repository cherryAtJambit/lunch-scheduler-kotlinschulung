package de.e2.spring.annotations

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AdresseAnnotationApplication

fun main(args: Array<String>) {
    runApplication<AdresseAnnotationApplication>(*args) //#TOPIC Spreading
}