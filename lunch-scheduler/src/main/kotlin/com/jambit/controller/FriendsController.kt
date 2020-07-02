package de.e2.lunch_scheduler.com.jambit.controller

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

const val AUTH_NAME = "basic" //TOPIC const

fun Application.friendsController() {


    routing {
        authenticate(AUTH_NAME) {
            get("/api/friends") {
                call.respondText("Hello")
            }
            post("/api/friends") {
                // TODO
            }
        }
    }
}
