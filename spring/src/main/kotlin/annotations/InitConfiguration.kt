package de.e2.spring.annotations

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class InitConfiguration {
    //In eigener Configuration, damit @WebMvcTest diese nicht lädt
    @Bean
    fun databaseInitializer(adresseRepository: AdresseRepository) = ApplicationRunner {
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