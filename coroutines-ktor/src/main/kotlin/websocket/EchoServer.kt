package de.e2.coroutines.websocket

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import org.slf4j.LoggerFactory

fun Application.echoWebsocket() {
    val logger = LoggerFactory.getLogger("server")
    install(WebSockets)
    routing {
        webSocket("/echo") {
            logger.info("Connected")

            for (message in incoming) {
                if (message is Frame.Text) {
                    val text = message.readText()
                    logger.info("Receive message $text")
                    outgoing.send(Frame.Text(text))
                }
            }

            logger.info("Disconnect")
        }
    }
}

fun main() {
    val server = embeddedServer(Netty, port = 8080, module = Application::echoWebsocket)
    server.start(wait = true)
}