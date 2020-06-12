package de.e2.ktor.multiplatform

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@OptIn(
    FlowPreview::class, KtorExperimentalAPI::class, ExperimentalCoroutinesApi::class
)
fun Application.broadcastWebsocket() {
    val logger = logger("server")
    install(WebSockets)
    val broadcastChannel = BroadcastChannel<String>(1)

    routing {
        webSocket("/broadcast") {
            logger.info("Connected")

            val subscriptionChannel = broadcastChannel.openSubscription()
            try {

                val broadcastJob = launch {
                    subscriptionChannel
                        .consumeEach { outgoing.send(Frame.Text(it)) }
                }

                incoming
                    .consumeAsFlow()
                    .mapNotNull { it as? Frame.Text }
                    .map { it.readText() }
                    .collect { message ->
                        logger.info("Receive message $message")
                        broadcastChannel.send(message)
                    }

                broadcastJob.cancel()

            } finally {
                subscriptionChannel.cancel()
            }

            logger.info("Disconnect")
        }
    }
}

fun Application.staticResources() {
    routing {
        static("/") {
            resource("main.js")
            defaultResource("index.html")
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8181, host = "127.0.0.1") {
        broadcastWebsocket()
        staticResources()
    }.start(wait = true)
}