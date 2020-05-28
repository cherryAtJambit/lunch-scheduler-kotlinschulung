package de.e2.misc.dsl

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.html.respondHtml
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.html.FlowOrPhrasingContent
import kotlinx.html.HTMLTag
import kotlinx.html.body
import kotlinx.html.svg
import kotlinx.html.unsafe



fun Application.main() {
    install(Routing) {
        get("/") {
            val svg = svg {
                width = 300
                rect(10, 20) {
                    stroke = RGB(0, 1, 1)
                    fill = RGB(255, 0, 0)
                }
                circle {
                    cx = 30
                }
            }

            call.respondHtml {
                body {
                    unsafe {
                        raw(svg)
                    }
                }
            }
        }

    }
}

fun main(args: Array<String>) {
    val server = embeddedServer(
        Netty, port = 8080, module = Application::main
    )
    server.start(wait = true)
}