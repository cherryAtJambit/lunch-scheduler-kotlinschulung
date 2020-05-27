package de.e2.springreactive.annotations

import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.DatabaseClient


@Configuration
class InitConfiguration {
    //In eigener Configuration, damit @WebMvcTest diese nicht lädt
    @Bean
    fun databaseInitializer(databaseClient: DatabaseClient,adresseRepository: AdresseRepository) = ApplicationRunner {
        databaseClient.execute("""
            CREATE TABLE adresse ( 
                id SERIAL PRIMARY KEY, 
                strasse VARCHAR(100) NOT NULL, 
                hausnummer VARCHAR(100) NOT NULL,
                plz VARCHAR(100) NOT NULL,
                stadt VARCHAR(100) NOT NULL
                );
        """).fetch().one().block()
        runBlocking {
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