package de.e2.ktor.sse

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.response.cacheControl
import io.ktor.response.respondText
import io.ktor.response.respondTextWriter
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.coroutines.time.delay
import mu.KotlinLogging
import java.time.Duration


fun Application.sse() {
    val logger = KotlinLogging.logger {}

    routing {
        get("/sse") {
            logger.info("Connect")
            call.response.cacheControl(CacheControl.NoCache(null))
            call.respondTextWriter(contentType = ContentType.Text.EventStream) {
                for (i in 1..100) {
                    logger.info("Event $i")
                    write("data: Event $i\n\n")
                    flush()
                    delay(Duration.ofSeconds(1))
                }
            }
        }
    }
}

fun main() {
    val server = embeddedServer(Netty, port = 8080, module = Application::sse)
    println("Open with: http://localhost:8080/sse")
    server.start(wait = true)
}


