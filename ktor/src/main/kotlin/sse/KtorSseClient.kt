package de.e2.ktor.sse

import mu.KotlinLogging
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget
import javax.ws.rs.sse.InboundSseEvent
import javax.ws.rs.sse.SseEventSource


fun main() {
    val logger = KotlinLogging.logger {}

    val client: Client = ClientBuilder.newBuilder().build()
    val target: WebTarget = client.target("http://localhost:8080/sse")
    val sseEventSource = SseEventSource.target(target).build()
    sseEventSource.register { event: InboundSseEvent ->
        val data = event.readData(String::class.java)
        logger.info(data)
    }
    sseEventSource.open()
    Thread.sleep(10000)
    sseEventSource.close()
    client.close()
}


