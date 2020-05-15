package de.e2.misc

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

fun sayHello() {
    println("Hello")
}

fun sayBye(): Unit {
    println("Bye")
}

fun riverOfNoReturn(): Nothing {
    throw RuntimeException("Schluss")
}

class Unit_und_Nothing : StringSpec({
    "Die besonderen Typen Unit und Nothing" {

        // sample functions declared below

        ::sayHello.returnType.classifier shouldBe Unit::class
        ::sayBye.returnType.classifier shouldBe Unit::class

        ::riverOfNoReturn.returnType.classifier shouldBe Nothing::class

        shouldThrow<RuntimeException> {
            riverOfNoReturn()

            println("Niemals")
        }
    }
})