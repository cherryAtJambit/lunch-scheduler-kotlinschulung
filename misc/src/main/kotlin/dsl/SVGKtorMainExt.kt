package de.e2.misc.dsl

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.html.respondHtml
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.html.HTMLTag
import kotlinx.html.body
import kotlinx.html.unsafe

fun Application.mainExt() {
    install(Routing) {
        get("/") {
            call.respondHtml {
                body {
                    svg {
                        width = 300
                        rect(10, 20) {
                            stroke = RGB(0, 1, 1)
                            fill = RGB(255, 0, 0)
                        }
                        circle {
                            cx = 30
                        }
                    }
                }
            }
        }

    }
}

fun HTMLTag.svg(body: SVG.() -> Unit) {
    unsafe {
        raw(de.e2.misc.dsl.svg(body))
    }
}

fun main(args: Array<String>) {
    val server = embeddedServer(
        Netty, port = 8080, module = Application::mainExt
    )
    server.start(wait = true)
}