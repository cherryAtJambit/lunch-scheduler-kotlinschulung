package de.e2.spring.annotations

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class InitConfiguration {
    @Autowired
    lateinit var adresseRepository: AdresseRepository //TOPIC Lateinit

    //In eigener Configuration, damit @WebMvcTest diese nicht lädt
    @Bean
    fun databaseInitializer() = ApplicationRunner {
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