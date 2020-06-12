package de.e2.ktor.multiplatform

fun main() {
    val chatClient = joinChat("Kotlin") { msg ->
        System.err.println(msg)
    }
    println("Insert message:")

    while (true) {
        val newMessage = readLine()
        if (newMessage == null || newMessage == "exit") {
            break;
        }
        chatClient.send(newMessage)
    }

    chatClient.job.cancel()
}