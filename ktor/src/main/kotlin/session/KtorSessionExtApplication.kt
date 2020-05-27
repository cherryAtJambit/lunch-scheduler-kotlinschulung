package de.e2.ktor.session

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.SessionSerializer
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Serializable
data class MySessionExt(
    val counter: Int,
    @Serializable(with = LocalDateTimeSerializer::class) val startDate: LocalDateTime = LocalDateTime.now()
)

@Serializer(forClass = LocalDateTime::class)
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_DATE_TIME))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), DateTimeFormatter.ISO_DATE_TIME)
    }
}


class MySessionSerializer : SessionSerializer<MySessionExt> {
    @OptIn(UnstableDefault::class)
    override fun deserialize(text: String) = Json.parse(MySessionExt.serializer(), text)

    @OptIn(UnstableDefault::class)
    override fun serialize(session: MySessionExt) = Json.stringify(
        MySessionExt.serializer(), session)

}

fun Application.sessionExt() {
    install(Sessions) {
        cookie<MySessionExt>("COOKIE_NAME") {
            serializer = MySessionSerializer()
        }

    }
    routing {
        get("/session") {
            var mySession = call.sessions.get<MySessionExt>()
            if (mySession == null) {
                mySession = MySessionExt(1)
            } else {
                mySession = mySession.copy(counter = mySession.counter + 1)
            }

            call.sessions.set(mySession)
            call.respondText("Session: $mySession")
        }
    }
}

fun main() {
    val server = embeddedServer(Netty, port = 8080, module = Application::sessionExt)
    println("Open with: http://localhost:8080/session")
    server.start(wait = true)
}


