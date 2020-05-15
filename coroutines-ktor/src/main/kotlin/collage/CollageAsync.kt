package de.e2.coroutines.collage

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.http.ContentType
import io.ktor.response.respondOutputStream
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


fun Application.collageAsync() {
    val ktorClient = HttpClient()
    routing {
        get("/collage/{query}") {
            val query = call.parameters["query"] ?: throw IllegalStateException("Query nicht angegeben")
            val count = call.request.queryParameters["count"]?.toInt() ?: 20

            val urls: List<String> = ktorClient.requestImageUrls(query, count)
            val deferredImages: List<Deferred<BufferedImage>> = urls.map {
                async {
                    ktorClient.requestImageData(it)
                }
            }

            val images: List<BufferedImage> = deferredImages.map { it.await() }
            val image = combineImages(images)

            call.respondOutputStream(ContentType.Image.PNG) {
                ImageIO.write(image, "png", this)
            }
        }
    }
}

fun main() {
    val server = embeddedServer(Netty, port = 8080, module = Application::collageAsync)
    println("Open with: http://localhost:8080/collage/turtle?count=12")
    server.start(wait = true)
}
