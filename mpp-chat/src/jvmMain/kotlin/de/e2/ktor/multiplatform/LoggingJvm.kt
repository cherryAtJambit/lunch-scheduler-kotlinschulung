package de.e2.ktor.multiplatform

import org.slf4j.LoggerFactory as SLF4JLoggerFactory

actual class Logger(name: String) {
    val logger = SLF4JLoggerFactory.getLogger(name)

    actual fun info(msg: String) = logger.info(msg)
    actual fun error(msg: String) = logger.error(msg)
}

actual fun logger(name: String) = Logger(name)