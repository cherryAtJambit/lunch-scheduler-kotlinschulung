package de.e2.coroutines

import de.e2.coroutines.collage.combineImages
import de.e2.coroutines.collage.requestImageData
import de.e2.coroutines.collage.requestImageUrls
import io.kotest.core.spec.style.StringSpec
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.selectUnbiased
import kotlinx.coroutines.time.delay
import org.glassfish.jersey.client.ClientConfig
import java.awt.image.BufferedImage
import java.io.FileOutputStream
import java.io.InputStream
import java.time.Duration
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import javax.imageio.ImageIO
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.InvocationCallback
import javax.ws.rs.core.MediaType
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@FlowPreview
@ExperimentalCoroutinesApi
class MoreCoroutine : StringSpec({

    "suspendCoroutine suspendiert die aktuelle Koroutine" {
        val jerseyClient = ClientBuilder.newClient(ClientConfig())
        suspend fun requestImageData(imageUrl: String) = suspendCoroutine<BufferedImage> { cont ->
            jerseyClient.target(imageUrl)
                .request(MediaType.APPLICATION_OCTET_STREAM)
                .async()
                .get(object : InvocationCallback<InputStream> {
                    override fun completed(response: InputStream) {
                        val image = ImageIO.read(response)
                        cont.resume(image)
                    }

                    override fun failed(throwable: Throwable) {
                        cont.resumeWithException(throwable)
                    }
                })
        }

        val image = requestImageData("https://etosquare.de/img/rene2018.jpg")
        println("${image.width}x${image.height}")
    }

    val ktorClient = HttpClient()

    "Channels"  {

        suspend fun retrieveImages(query: String, channel: SendChannel<BufferedImage>) {
            while (isActive) {
                try {
                    val url = ktorClient.requestImageUrls(query,1).random()
                    val image = ktorClient.requestImageData(url)
                    channel.send(image)
                    delay(Duration.ofSeconds(2))
                } catch (exc: Exception) {
                    delay(Duration.ofSeconds(2))
                }
            }
        }

        suspend fun createCollage(channel: ReceiveChannel<BufferedImage>, count: Int) {
            var imageId = 0
            while (isActive) {
                val images = (1..count).map {
                    channel.receive()
                }
                val collage = combineImages(images)
                ImageIO.write(collage, "png", FileOutputStream("image-${imageId++}.png"))
            }
        }

        val channel = Channel<BufferedImage>()
        val dogsJob = launch(Dispatchers.Unconfined) {
            retrieveImages("dogs", channel)
        }

        val catsJob = launch(Dispatchers.Unconfined) {
            retrieveImages("cats", channel)
        }

        val collageJob = launch(Dispatchers.Unconfined) {
            createCollage(channel, 4)
        }
        delay(Duration.ofMinutes(1))

        dogsJob.cancel()
        catsJob.cancel()
        collageJob.cancel()
    }


    "Producer und Consumer"  {
        suspend fun retrieveImages(query: String): ReceiveChannel<BufferedImage> = produce {
            while (isActive) {
                try {
                    val url = ktorClient.requestImageUrls(query).random()
                    val image = ktorClient.requestImageData(url)
                    send(image)
                    delay(Duration.ofSeconds(2))
                } catch (exc: Exception) {
                    delay(Duration.ofSeconds(1))
                }
            }
        }

        suspend fun createCollage(count: Int, vararg channels: ReceiveChannel<BufferedImage>): BufferedImage {
            val images = (1..count).map {
                selectUnbiased<BufferedImage> {
                    channels.forEach { channel ->
                        channel.onReceive { it }
                    }
                }
            }
            return combineImages(images)
        }

        val dogsChannel = retrieveImages("dogs")
        val catsChannel = retrieveImages("cats")

        val collageJob = launch(Dispatchers.Unconfined) {
            var imageId = 0
            while (isActive) {
                val collage = createCollage(4, catsChannel, dogsChannel)
                ImageIO.write(collage, "png", FileOutputStream("image-${imageId++}.png"))
            }
        }
        delay(Duration.ofMinutes(2))

        dogsChannel.cancel()
        catsChannel.cancel()
        collageJob.cancel()
    }

    "Broadcast Channels"  {
        val channel = Channel<String>()

        repeat(5) { index ->
            launch {
                for (msg in channel) {
                    println("Channel empfangen $index: $msg")
                }
            }
        }

        channel.send("A")
        channel.send("B")
        channel.send("C")
        channel.cancel()

        val broadcastChannel = BroadcastChannel<String>(1)

        repeat(5) { index ->
            launch {
                for (msg in broadcastChannel.openSubscription()) {
                    println("BroadcastChannel empfangen $index: $msg")
                }
            }
        }

        delay(Duration.ofSeconds(1))
        broadcastChannel.send("A")
        broadcastChannel.send("B")
        broadcastChannel.send("C")
        broadcastChannel.cancel()
    }

    "Producer und Consumer mit Flows"  {
        fun retrieveImages(query: String): Flow<BufferedImage> = flow {
            while(true) {
                try {
                    val url = ktorClient.requestImageUrls(query).random()
                    val image = ktorClient.requestImageData(url)
                    println("$query new image")
                    emit(image)
                    delay(Duration.ofSeconds(1))
                } catch (exc: Exception) {
                    delay(Duration.ofSeconds(1))
                }
            }
        }

        fun createCollage(count: Int, vararg flows: Flow<BufferedImage>): Flow<BufferedImage> = flow {
            val imageFlow = flowOf(*flows).flattenMerge()
            val images = ConcurrentLinkedQueue<BufferedImage>()
            imageFlow.collect {
                println(Thread.currentThread().name)
                images+=it
                if(images.size == count) {
                    println("new collage")
                    emit(combineImages(images))
                    images.clear()
                }
            }
        }

        val dogsFlow = retrieveImages("dogs")
        val catsFlow = retrieveImages("cats")

        val collageJob = launch(Dispatchers.Unconfined) {
            createCollage(4, catsFlow, dogsFlow).collectIndexed { imageId, collage ->
                println("new png")
                ImageIO.write(collage, "png", FileOutputStream("image-${imageId}.png"))
            }
        }
        delay(Duration.ofMinutes(2))
        collageJob.cancel()
    }
})