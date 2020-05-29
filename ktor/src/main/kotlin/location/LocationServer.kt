@file:UseExperimental(KtorExperimentalLocationsAPI::class)

package de.e2.ktor.location

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.html.respondHtml
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.locations.locations
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.title

@Location("/")
class Index()

@Location("/hello/{name}")
class Hello(val name: String)

fun Application.main() {
    install(Locations)

    routing {
        get<Index> {
            call.respondHtml {
                head {
                    title {
                        text("Locations")
                    }
                }
                body {
                    h1 {
                        text("Greeter")
                    }
                    div {
                        //Extension-Property an PipelineContext
                        a(locations.href(Hello("Rene"))) {
                            text("Say Hello")
                        }
                    }
                }
            }
        }

        //Extension-Funktion für Route
        get<Hello> { hello ->
            call.respondHtml {
                head {
                    title {
                        text("Hello")
                    }
                }
                body {
                    h1 {
                        text("Hello ${hello.name}")
                    }
                }
            }
        }
    }
}

fun main() {
    val server = embeddedServer(
        Netty, port = 8080, module = Application::main
    )
    server.start(wait = true)
}