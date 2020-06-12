package de.e2.ktor.multiplatform

expect class Logger {
    fun info(msg: String)
    fun error(msg: String)
}

expect fun logger(name: String): Logger