package de.e2.lunch_scheduler

import de.e2.lunch_scheduler.com.jambit.controller.AUTH_NAME
import de.e2.lunch_scheduler.com.jambit.controller.friendsController
import de.e2.lunch_scheduler.com.jambit.controller.meController
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val server = embeddedServer(factory = Netty, port = 8080) {

        install(ContentNegotiation) {
            jackson()
        }

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

        friendsController()
        meController()
    }
    server.start(wait = true)
}
