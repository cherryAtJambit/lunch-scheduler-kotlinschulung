package de.e2.ktor.rest.jackson

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

data class MyRequest(val content: String)

data class MyResponse(val content: String)


fun Application.rest() {
    install(ContentNegotiation) {
        jackson()
    }

    routing {
        post("/rest") {
            val request = call.receive<MyRequest>()
            call.respond(MyResponse(request.content.reversed()))
        }
    }
}

fun main() {
    val server = embeddedServer(Netty, port = 8080, module = Application::rest)
    println("Open with: http://localhost:8080/rest")
    server.start(wait = true)
}


