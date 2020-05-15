package de.e2.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import java.time.Duration
import java.util.concurrent.Executors

class BasicCoroutine : StringSpec({
    "runBlocking" {
        val result = runBlocking {
            println("start")
            delay(Duration.ofSeconds(1))
            println("ende")
            42
        }

        result shouldBe 42
    }

    "test are suspendable" {
        println("start")
        delay(Duration.ofSeconds(1))
        println("ende")
    }

    "launch to start a parallel job" {
        val job = launch {
            for (index in 1..10) {
                println("Run $index from Thread ${Thread.currentThread().name}")
                delay(Duration.ofSeconds(1))
            }
        }

        delay(Duration.ofSeconds(5))
        job.cancel()
        job.join()
        job.isCancelled shouldBe true
    }

    "async startet asynchrone Koroutine die ein Ergebnis liefert"  {

        val results: List<Deferred<Int>> = (0..9).map { index ->
            async {
                println("Start $index from Thread ${Thread.currentThread().name}")
                delay(Duration.ofSeconds(1))
                println("End $index from Thread ${Thread.currentThread().name}")
                index
            }
        }

        results.forEachIndexed { index, result ->
            val i = result.await()
            println("Result $i from Thread ${Thread.currentThread().name}")
            i shouldBe index
        }
    }

    /**
     * Beim Starten einer Koroutine kann ein CoroutinenContext angegeben werden.
     * Dieser ist besteht aus einer Menge von Eigenschaften, z.B. den Dispatcher der festlegt,
     * mit welchem Thread die Koroutine läuft
     */
    "CoroutineContext konfigurieren"  {

        val job = launch(Dispatchers.Default) {
            for (index in 1..10) {
                println("Run $index from Thread ${Thread.currentThread().name}")
                delay(Duration.ofSeconds(1))
            }
        }

        delay(Duration.ofSeconds(5))
        println("Run from Thread ${Thread.currentThread().name}")
        job.cancel()
        job.join()
        job.isCancelled shouldBe true
    }

    "CoroutineContext beim Ablauf wechseln"  {

        val coroutineDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

        for (index in 1..10) {
            val resultFromOtherContext = withContext(coroutineDispatcher) {
                println("Run $index from Thread ${Thread.currentThread().name}")
                index * 10
            }

            println("Result $resultFromOtherContext from Thread ${Thread.currentThread().name}")
            resultFromOtherContext shouldBe index * 10
            delay(Duration.ofSeconds(1))
        }
    }

    /**
     * * Neues Konzept seit Version 0.26.0
     * ([Theorie](https://vorpus.org/blog/notes-on-structured-concurrency-or-go-statement-considered-harmful/),
     * [Umsetzung in Kotlin](https://medium.com/@elizarov/structured-concurrency-722d765aa952))
     *
     * * Die Idee dabei ist, dass Koroutinen die weitere Koroutinen starten (Kinder), selber erst beendet werden, wenn alle Kinder
     *   fertig sind. Falls ein Kind eine Exception nicht behandelt und deswegen beendet wird,
     *   werden dessen eigene Kinder, alle Geschwister und die Eltern-Koroutine ebenso beendet.
     *
     * * Das Ziel dabei ist, ähnlich wie bei strukturierter Programmierung, Klammern (Scopes) zu bilden,
     *   die garantieren, dass keine Koroutine unabsichtlich noch läuft.
     *
     * * Deswegen sind die `Builder` zum starten von neuen Koroutinen als globale Funktionen entfernt worden und müssen
     *   in der neuen Version immer an einem Scope aufgerufen werden.
     *   Ausnahme bildet der `GlobalScope` derren Kinder keine Abhängigkeit zu einer Eltern-Koroutine haben.
     */
    "Structured Concurrency und CoroutineScope"  {

        val result = coroutineScope {
            launch {
                delay(Duration.ofSeconds(1))
                println("Erster fertig")
            }

            launch {
                delay(Duration.ofSeconds(1))
                println("Zweiter fertig")
            }

            println("Return result")
            42
        }

        println("Receive Result $result")
    }

    "Structured Concurrency und Fehler" {
        //Scope 1
        shouldThrow<IllegalStateException> {
            val result = coroutineScope {

                for (index in 1..10) {

                    //Scope 1.1 - 1.10
                    launch {
                        delay(Duration.ofSeconds(index.toLong()))
                        println("$index fertig")
                    }

                }

                //Scope 1.11
                launch {
                    delay(Duration.ofSeconds(3))
                    throw IllegalStateException()
                }

                println("Return result")
                42
            }

            println("Receive Result $result")
        }
    }

    /**
     * Der SupervisorScope stoppt keine anderen Koroutinen wenn ein Kind abbricht.
     */
    "Structured Concurrency und SupervisorScope" {

        //Scope 1 ist ein SupervisorScope
        val result = supervisorScope {

            for (index in 1..10) {

                //Scope 1.1 - 1.10
                launch {
                    delay(Duration.ofSeconds(index.toLong()))
                    println("$index fertig")
                }

            }

            //Scope 1.11
            launch {
                delay(Duration.ofSeconds(3))
                throw IllegalStateException()
            }

            println("Return result")
            42
        }

        println("Receive Result $result")
    }

    /**
     * Insbesondere bei UI Applikationen ist es sinnvol die Lebenszeit der Koroutinen
     * an die Lebenszeit der UI Element zu binden, die benötigt werden
     */
    "Eigenen Scope definieren und an Lebenszeit eines Objektes binden" {

        class ObjectWithLifecycle(val id: Int) : CoroutineScope by CoroutineScope(Dispatchers.Default) {
            fun close() {
                //Alle Koroutinen stoppen und keine neuen zulassen
                cancel()
            }

            fun launchForObject(msg: String) = launch {
                delay(Duration.ofSeconds(1))
                println("Hallo vom Objekt $id mit Nachricht $msg")
            }
        }

        val object1 = ObjectWithLifecycle(1)
        val object2 = ObjectWithLifecycle(2)

        for (i in 1..10) {
            object1.launchForObject("AAA")
            object2.launchForObject("BBB")
        }

        delay(Duration.ofMillis(500))
        object1.close()
        object1.launchForObject("Wird nicht mehr gestartet")

        delay(Duration.ofSeconds(2))
    }

    /**
     * Wenn das normale Fehlerverhalten von CoroutinenScopes nicht passt kann man den SupervisorJob als ParentJob benutzen
     */
    "SupervisorJob als ParentJob" {
        class ObjectWithLifecycle(val id: Int, superVisorJob: Boolean) :
            CoroutineScope by CoroutineScope(if (superVisorJob) SupervisorJob() else Job()) {
            fun close() {
                //Alle Koroutinen stoppen und keine neuen zulassen
                cancel()
            }

            fun launchForObject(msg: String) = launch {
                try {
                    delay(Duration.ofSeconds(1))
                    println("Hallo vom Objekt $id mit Nachricht $msg")
                } catch (e: Exception) {
                    println("Catch exc: $e")
                    throw e
                }
            }

            fun launchWithException() = launch {
                throw IllegalStateException("Fail")
            }
        }

        val object1 = ObjectWithLifecycle(1, false)
        val object2 = ObjectWithLifecycle(2, true)

        for (i in 1..10) {
            object1.launchForObject("AAA")
            object2.launchForObject("BBB")
        }

        object1.launchWithException()
        object2.launchWithException()

        delay(Duration.ofSeconds(2))
    }
})