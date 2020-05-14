package de.e2.koin

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.modules

fun main() {
    val server = embeddedServer(factory = Netty, port = 8080) {

        modules(personModule)

        val personService: PersonService by inject()
        routing {

            get("/load") {

                call.respondText("load:" + personService.load())
            }
        }
    }
    server.start(wait = true)
}