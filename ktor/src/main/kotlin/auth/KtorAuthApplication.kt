package de.e2.ktor.auth

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.auth.principal
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

const val AUTH_NAME = "basic" //TOPIC const

fun Application.auth() {
    install(Authentication) {
        basic(name = AUTH_NAME) {
            realm = "Ktor Server"
            validate { credentials ->
                if (credentials.name == credentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    routing {
        authenticate(AUTH_NAME) {
            get("/auth") {
                val principal = call.principal<UserIdPrincipal>()
                call.respondText("auth with ${principal?.name}")
            }
        }
        get("/") {
            val principal = call.principal<UserIdPrincipal>()
            call.respondText("auth with ${principal?.name}")
        }
    }
}

fun main() {
    val server = embeddedServer(Netty, port = 8080, module = Application::auth)
    println("Open with: http://localhost:8080/auth")
    println("Open with: http://localhost:8080/")
    server.start(wait = true)
}


