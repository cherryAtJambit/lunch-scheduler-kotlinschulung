package de.e2.lunch_scheduler.com.jambit.controller

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing


fun Application.meController() {

    routing {
        authenticate(AUTH_NAME) {
            get("/api/me") {
                val principal = call.principal<UserIdPrincipal>()
                call.respondText("Authorised with user: ${principal?.name}")
            }
        }
    }
}
