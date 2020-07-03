package de.e2.spring.lunchScheduler

import de.e2.spring.annotations.AdresseAnnotationApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class User(val name: String, val id: String? = null)
data class Friendship(val userId: String, val friendUserId: String, val id: String? = null)

@SpringBootApplication
class LunchApplication

@RestController
@RequestMapping("/api")
class LunchController() {

    @GetMapping("/me", produces = arrayOf(APPLICATION_JSON_VALUE))
    fun getMe(): User {

        val user = User("Cherry")

        return user
    }
}

fun main(args: Array<String>) {
    runApplication<LunchApplication>(*args)
}