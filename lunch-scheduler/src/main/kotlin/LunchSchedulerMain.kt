package de.e2.lunch_scheduler

import de.e2.lunch_scheduler.com.jambit.controller.friendsController
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val server = embeddedServer(factory = Netty, port = 8080) {
        friendsController()
    }
    server.start(wait = true)
}
