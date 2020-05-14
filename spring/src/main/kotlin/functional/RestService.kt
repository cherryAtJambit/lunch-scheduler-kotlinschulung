package de.e2.spring.functional

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.router

fun router(adresseRepository: AdresseRepository) = router {
    "/api/adresse".nest {
        accept(MediaType.APPLICATION_JSON).nest {
            GET("/") {
                ok().body(adresseRepository.findAll())
            }

            GET("/{id}") {
                val id = it.pathVariable("id").toLong()
                val adresse = adresseRepository.findByIdOrNull(id)
                adresse?.let { ok().body(adresse) }
                    ?: status(HttpStatus.NOT_FOUND).body("This address does not exist")
            }
        }
    }
}