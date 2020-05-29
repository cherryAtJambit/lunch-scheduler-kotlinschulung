package de.e2.ktor.p04_echo.echo_2_broadcast

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

@OptIn(KtorExperimentalAPI::class)
fun main() = runBlocking {
    val client = HttpClient() {
        install(WebSockets)
    }

    repeat(3) { clientId ->
        launch {
            client.ws(host = "127.0.0.1", port = 8080, path = "/broadcast") {
                val logger = LoggerFactory.getLogger("client.$clientId")

                launch {
                    for (message in incoming) {
                        if (message is Frame.Text) {
                            logger.info(message.readText())
                        }
                    }
                }

                outgoing.send(Frame.Text("Message from $clientId"))

                delay(2000)
            }
        }
    }

    delay(5000)
}