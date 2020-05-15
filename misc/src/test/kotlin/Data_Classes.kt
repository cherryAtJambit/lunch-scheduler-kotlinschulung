package de.e2.misc

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * ## Data Classes
 *
 * In Java ist das Entwurfsmuster *Data Transfer Object* verbreitet.
 * Dabei geht es um Objekte, die meist keine eigene Fachlogik besitzen und
 * primär Träger von Informationen sind.
 *
 * In Kotlin gibt es dafür syntaktische Unterstützung: `data class`.
 *
 * * Haben Felder (`val`/`var`) wie andere Klassen.
 *
 * * Haben zusätzlich automatische Implementierungen für
 *   - `equals(...)`
 *   - `hashCode(...)`
 *   - `copy(...)`
 *   - und mehr
 *
 * * Können oft in einer Zeile deklariert werden
 *
 * * Ersparen viel Boilerplate-Code
 *
 */
class Data_Classes : StringSpec({

    "Data Class" {

        data class Rechteck(val breite: Int, val hoehe: Int)


        val r = Rechteck(3, 4)

        r.toString() shouldBe "Rechteck(breite=3, hoehe=4)"

        r shouldBe Rechteck(3, 4)
        r shouldNotBe Rechteck(11, 4)
        r shouldNotBe Rechteck(3, 11)
    }

    /***
     * ### Die `copy`-Methode bei Data Classes
     *
     * * Erzeugt eine Kopie, bei der einige Feldwert anders besetzt sind.
     *
     * * Besonders nützlich für den Umgang mit *Immutable Value Objects*.
     *
     */
    "Kopieren von Datenobjekten" {

        data class Name(val vorname: String, val nachname: String)

        val n1 = Name("Bjørn", "Stachmann")

        val n2 = n1.copy(vorname = "René")
        val n3 = n2.copy(nachname = "Preißel")


        n1 shouldBe Name("Bjørn", "Stachmann")
        n2 shouldBe Name("René", "Stachmann")
        n3 shouldBe Name("René", "Preißel")
    }

    /**
     * ### Data Classes als Rückgabewert von Funktionen
     *
     * *Data Classes* eignen sich als Rückgabewert für Funktionen,
     * die mehrere Ergebnisse liefern.
     * Per *Destructuring* können die Felder im Rückgabewert unterschiedlichen
     * Variablen zugewiesen werden.
     */
    "Data Classes and Destructuring A" {

        data class ShellOutput(val out: String, val err: String, val exitCode: Int)

        fun callExternalScript(): ShellOutput {
            // call shell script and capture output
            return ShellOutput("fake-output", "fake-error-message", 42)
        }


        val (stdout, stderr, exit) = callExternalScript()


        stdout shouldBe "fake-output"
        stderr shouldBe "fake-error-message"
        exit shouldBe 42
    }

    "Data Classes and Destructuring B" {

        fun divWithRemainder(a: Long, b: Long): Pair<Long, Long> {
            return Pair(a / b, a % b)
        }

        val r1 = divWithRemainder(10, 3)
        println(r1.first)
        println(r1.second)

        val (erg, rest) = divWithRemainder(10, 3)
        println(erg)
        println(rest)
    }
})