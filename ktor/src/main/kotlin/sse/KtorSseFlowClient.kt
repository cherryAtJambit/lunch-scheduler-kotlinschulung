package de.e2.ktor.sse

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.take
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import mu.KotlinLogging
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget
import javax.ws.rs.sse.InboundSseEvent
import javax.ws.rs.sse.SseEventSource

@OptIn(ExperimentalCoroutinesApi::class)
fun sseFlow(url: String) = callbackFlow<String> {
    val client: Client = ClientBuilder.newBuilder().build()
    val target: WebTarget = client.target(url)
    val sseEventSource = SseEventSource.target(target).build()
    sseEventSource.register { event: InboundSseEvent ->
        val data = event.readData(String::class.java)
        offer(data)
    }
    sseEventSource.open()

    awaitClose {
        sseEventSource.close()
        client.close()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun main() {
    val logger = KotlinLogging.logger {}
    val flow = sseFlow("http://localhost:8080/sse")
    flow.take(10).collect { data ->
        logger.info(data)
    }

    flow.take(10).collect { data ->
        logger.info(data)
    }
}


