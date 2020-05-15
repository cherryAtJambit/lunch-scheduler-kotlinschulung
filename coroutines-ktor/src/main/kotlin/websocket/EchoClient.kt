package de.e2.ktor.p04_echo.echo_1_websocket

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory


@OptIn(
    KtorExperimentalAPI::class, ExperimentalCoroutinesApi::class, FlowPreview::class
)
fun main() = runBlocking {
    val client = HttpClient() {
        install(WebSockets)
    }

    repeat(3) { clientId ->
        launch {

            client.ws(host = "127.0.0.1", port = 8080, path = "/echo") {
                val logger = LoggerFactory.getLogger("client.$clientId")
                logger.info("Connected")

                val sendJob = launch {
                    while (isActive) {
                        delay(1000)
                        logger.info("Send hello")
                        outgoing.send(Frame.Text("Hello from $clientId"))
                    }
                }

                incoming
                    .consumeAsFlow()
                    .mapNotNull { it as? Frame.Text }
                    .map { it.readText() }
                    .take(3)
                    .onCompletion { sendJob.cancel() }
                    .collect {
                        logger.info("received $it")
                    }

                logger.info("Disconnect")
            }
        }
    }


}