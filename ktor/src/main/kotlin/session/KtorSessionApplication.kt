package de.e2.ktor.session

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set


data class MySession(val counter: Int)


fun Application.session() {
    install(Sessions) {
        cookie<MySession>("COOKIE_NAME")
    }
    routing {
        get("/session") {
            var mySession = call.sessions.get<MySession>()
            if (mySession == null) {
                mySession = MySession(1)
            } else {
                mySession = mySession.copy(counter = mySession.counter + 1)
            }

            call.sessions.set(mySession)
            call.respondText("Session: $mySession")
        }
    }
}

fun main() {
    val server = embeddedServer(Netty, port = 8080, module = Application::session)
    println("Open with: http://localhost:8080/session")
    server.start(wait = true)
}


