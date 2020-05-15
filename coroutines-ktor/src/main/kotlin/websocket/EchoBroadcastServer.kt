package de.e2.ktor.p04_echo.echo_2_broadcast

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

@OptIn(
    FlowPreview::class, KtorExperimentalAPI::class, ExperimentalCoroutinesApi::class
)
fun Application.broadcastWebsocket() {
    val logger = LoggerFactory.getLogger("server")
    install(WebSockets)
    val broadcastChannel = BroadcastChannel<String>(1)

    routing {
        webSocket("/broadcast") {
            logger.info("Connected")

            val broadcastJob = launch {
                broadcastChannel.asFlow().onCompletion {
                    println("Finished listening $it")
                }.collect { message ->
                    outgoing.send(Frame.Text(message))
                }
            }

            incoming
                .consumeAsFlow()
                .mapNotNull { it as? Frame.Text }
                .map { it.readText() }
                .onCompletion { broadcastJob.cancel() }
                .collect { text ->
                    logger.info("Receive message $text")
                    broadcastChannel.send(text)
                }

            logger.info("Disconnect")
        }
    }
}

@KtorExperimentalAPI
fun main() {
    val server = embeddedServer(Netty, port = 8080, module = Application::broadcastWebsocket)
    server.start(wait = true)
}