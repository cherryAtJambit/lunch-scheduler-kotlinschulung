package de.e2.ktor.sse

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.take
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget
import javax.ws.rs.sse.InboundSseEvent
import javax.ws.rs.sse.SseEventSource

class SseChannel private constructor(
    val url: String,
    private val channel: Channel<String>
) : ReceiveChannel<String> by channel {

    private var client: Client? = null
    private var sseEventSource: SseEventSource? = null

    constructor(url: String) : this(url, Channel(Channel.UNLIMITED))

    fun start(scope: CoroutineScope) {
        if (client != null) {
            throw IllegalStateException()
        }
        val client = ClientBuilder.newBuilder().build()
        val target: WebTarget = client.target(url)
        val sseEventSource = SseEventSource.target(target).build()
        sseEventSource.register { event: InboundSseEvent ->
            val data = event.readData(String::class.java)
            scope.launch {
                channel.send(data)
            }
        }
        sseEventSource.open()
        this.client = client
        this.sseEventSource = sseEventSource
    }

    fun stop() {
        sseEventSource?.close()
        client?.close()

        sseEventSource = null
        client = null
    }
}

suspend fun main() {
    val logger = KotlinLogging.logger {}
    coroutineScope {
        val sseChannel = SseChannel("http://localhost:8080/sse")
        sseChannel.start(this)
        for (data in sseChannel.take(10)) {
            logger.info(data)
        }
        sseChannel.stop()
    }
}


