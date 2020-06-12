package de.e2.ktor.multiplatform

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onReceiveOrNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.whileSelect
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@Serializable
data class ChatMessage(val nickName: String, val message: String)

class ChatClient(val job: Job, val send: (String) -> Unit)

//<editor-fold desc="">
@OptIn(
    InternalCoroutinesApi::class, KtorExperimentalAPI::class, ExperimentalCoroutinesApi::class
)
//</editor-fold>
fun joinChat(nickName: String, onReceive: (String) -> Unit): ChatClient {
    //<editor-fold desc="variablen" defaultstate="collapsed">
    val client = HttpClient() {
        install(WebSockets)
    }
    val logger = logger("chatclient.$nickName")
    val json = Json(JsonConfiguration.Stable)
    val job = Job()
    val clientScope = CoroutineScope(job)
    val sendingChannel = Channel<String>(Channel.UNLIMITED)
    //</editor-fold>

    clientScope.launch {
        while (isActive) {
            supervisorScope {
                try {
                    client.ws(host = "127.0.0.1", port = 8181, path = "/broadcast") {
                        logger.info("Connected with server")
                        //<editor-fold desc="Chat Handling" defaultstate="collapsed">
                        whileSelect {
                            incoming.onReceiveOrClosed { voc ->
                                val message =  voc.valueOrNull
                                if (message is Frame.Text) {
                                    val chatMessage = json.parse(message.readText())
                                    logger.info("received nessage $chatMessage")

                                    val formattedMessage = if (nickName == chatMessage.nickName) {
                                        "Me: ${chatMessage.message}"
                                    } else {
                                        "${chatMessage.nickName}: ${chatMessage.message}"
                                    }

                                    onReceive(formattedMessage)
                                }
                                message != null
                            }

                            sendingChannel.onReceive { msg ->
                                val jsonMsg = json.stringify(ChatMessage(nickName, msg))
                                outgoing.send(Frame.Text(jsonMsg))
                                true
                            }
                        }
                        //</editor-fold>
                    }
                } catch (exc: Exception) {
                    logger.error("Exception with websocket $exc")
                }
            }
            logger.info("Disconnected. Reconnect after 5 second")
            delay(5000)
        }
    }

    return ChatClient(job) { msg ->
        if (!job.isActive)
            throw IllegalStateException("Client closed")

        clientScope.launch {
            sendingChannel.send(msg)
        }
    }
}

//<editor-fold desc="Json functions" defaultstate="collapsed">
fun Json.parse(json : String) = parse(ChatMessage.serializer(), json)
fun Json.stringify(chatMessage: ChatMessage) = stringify(ChatMessage.serializer(), chatMessage)
//</editor-fold>