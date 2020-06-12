package de.e2.ktor.multiplatform

import kotlin.reflect.KClass

actual class Logger(val name: String) {
    actual fun info(msg: String) {
        console.info("$name: $msg")
    }
    actual fun error(msg: String) {
        console.error("$name: $msg")
    }
}

actual fun logger(name: String) = Logger(name)