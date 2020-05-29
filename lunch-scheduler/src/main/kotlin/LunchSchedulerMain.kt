package de.e2.lunch_scheduler

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.modules

fun Application.lunchScheduler() {
    routing {
        get("/") {
            call.respondText("TODO")
        }
    }
}

fun main() {
    val server = embeddedServer(factory = Netty, port = 8080, module = Application::lunchScheduler)
    server.start(wait = true)
}