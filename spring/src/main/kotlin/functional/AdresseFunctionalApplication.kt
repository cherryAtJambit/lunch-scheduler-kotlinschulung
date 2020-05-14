package de.e2.spring.functional

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans

@SpringBootApplication
class AdresseFunctionalApplication

fun main(args: Array<String>) {
    runApplication<AdresseFunctionalApplication>(*args) {
        addInitializers(
            beans {
                bean(::router)
                bean {
                    val adresseRepository = ref<AdresseRepository>()
                    ApplicationRunner {
                        for (i in 1..10) {
                            adresseRepository.save(
                                Adresse(
                                    "Hauptstrasse",
                                    "$i",
                                    "22000",
                                    "Hamburg"
                                )
                            )
                        }
                    }
                }
            }
        )
    }
}