package de.e2.coroutines

import de.e2.coroutines.collage.combineImages
import de.e2.coroutines.collage.requestImageData
import de.e2.coroutines.collage.requestImageUrls
import io.kotest.core.spec.style.StringSpec
import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onErrorReturn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
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
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@FlowPreview
@ExperimentalCoroutinesApi
class MoreCoroutine : StringSpec({


    val ktorClient = HttpClient()


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
        delay(Duration.ofSeconds(10))

        dogsChannel.cancel()
        catsChannel.cancel()
        collageJob.cancel()
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
                    when(exc) {
                        is CancellationException -> {
                            println("Cancelled $query")
                            throw exc
                        }
                        else ->  delay(Duration.ofSeconds(1))
                    }
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

        val dogsFlow = retrieveImages("dogs").onStart { println("Started dogs") }
        val catsFlow = retrieveImages("cats").onStart { println("Started cats") }

        val collageJob = launch(Dispatchers.Unconfined) {
            val collageFlow = createCollage(4, catsFlow, dogsFlow)
                .onStart { println("Started collage") }
                .onCompletion { println("Completed collage") }
            collageFlow.collectIndexed { imageId, collage ->
                println("new png")
                ImageIO.write(collage, "png", FileOutputStream("image-${imageId}.png"))
            }
        }
        delay(Duration.ofSeconds(10))
        println("Cancel Job")
        collageJob.cancel()
    }

    "Callback Flows"  {
        fun flowFromCallback() = callbackFlow<String> {
            val thread = thread {
                println("Start Thread")
                var i = 0
                try{
                    while (!Thread.interrupted()) {
                        offer("Event ${i++}")
                        Thread.sleep(100)
                    }
                } finally {
                    println("Stop Thread")
                }
            }

            awaitClose {
                thread.interrupt()
            }
        }

        val flow = flowFromCallback()
        flow.take(5).collect {
            println(it)
        }
    }

    "Broadcast Channel mit Flow"  {
        val broadcastChannel = BroadcastChannel<String>(1)
        val broadcastFlow = broadcastChannel.asFlow()

        repeat(5) { index ->
            launch {
                broadcastFlow.collect {msg ->
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


})