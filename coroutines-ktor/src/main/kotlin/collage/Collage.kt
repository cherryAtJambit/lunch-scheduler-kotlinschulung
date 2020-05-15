package de.e2.coroutines.collage

import com.jayway.jsonpath.JsonPath
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.get
import io.ktor.client.statement.HttpStatement
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondOutputStream
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.lang.IllegalStateException
import javax.imageio.ImageIO

fun Application.collage() {
    val ktorClient = HttpClient()
    routing {
        get("/collage/{query}") {
            val query = call.parameters["query"] ?: throw IllegalStateException("missing query parameter")
            val count = call.parameters["count"]?.toInt() ?: 20

            val urls: List<String> = ktorClient.requestImageUrls(query, count)
            val images: List<BufferedImage> = urls.map { ktorClient.requestImageData(it) }
            val image = combineImages(images)

            call.respondOutputStream(ContentType.Image.PNG) {
                ImageIO.write(image, "png", this)
            }
        }
    }
}

suspend fun HttpClient.requestImageUrls(query: String, count: Int = 20): List<String> {
    val json = get<String>("https://api.qwant.com/api/search/images?offset=0&t=images&uiv=1&q=$query&count=$count")
    return JsonPath.read<List<String>>(json, "$..thumbnail").map { "https:$it" }
}

suspend fun HttpClient.requestImageData(imageUrl: String): BufferedImage {
    val httpStatement = get<HttpStatement>(imageUrl)
    return httpStatement.execute { response ->
        if (response.status == HttpStatusCode.OK) {
            ImageIO.read(ByteArrayInputStream(response.receive<ByteArray>()))
        } else {
            throw IOException("Wrong status code ${response.status}")
        }
    }
}

fun combineImages(imageList: Collection<BufferedImage>): BufferedImage {
    if (imageList.isEmpty()) {
        return BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR)
    }

    val yDim = Math.sqrt(imageList.size.toDouble()).toInt()
    val xDim = (imageList.size + yDim - 1) / yDim

    val maxDim = imageList.asSequence().map { Pair(it.width, it.height) }.fold(Pair(0, 0)) { a, b ->
        Pair(maxOf(a.first, b.first), maxOf(a.second, b.second))
    }

    val newImage = BufferedImage(maxDim.first * xDim, maxDim.second * yDim, BufferedImage.TYPE_3BYTE_BGR)
    val graphics = newImage.graphics
    graphics.color = Color.WHITE
    graphics.fillRect(0, 0, newImage.width, newImage.height)

    imageList.forEachIndexed { index, subImage ->
        val x = index % xDim
        val y = index / xDim
        val posX = maxDim.first * x + (maxDim.first - subImage.width) / 2
        val posY = maxDim.second * y + (maxDim.second - subImage.height) / 2
        graphics.drawImage(subImage, posX, posY, null)
    }
    return newImage
}

fun main() {
    val server = embeddedServer(Netty, port = 8080, module = Application::collage)
    println("Open with: http://localhost:8080/collage/turtle?count=12")
    server.start(wait = true)
}
