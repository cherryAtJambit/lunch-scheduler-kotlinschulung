package de.e2.misc

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import kotlin.math.PI
import kotlin.math.round
import io.kotest.assertions.throwables.shouldThrow as shouldThrow1

/**
 * ## Klassen in Kotlin
 *
 * - Besonderheiten bei der Deklaration
 * - Objekte (Instanzen) erzeugen.
 * - Primäre und sekundäre Konstruktoren
 * - Vererbung
 *
 */
class Klasssen_und_Objekte : StringSpec({

    /**
     * ## Properties und Funktionen
     *
     *  * *Properties* als `val` oder `var`
     *
     *  * *Funktionen* (aka *Methoden*) als `fun`
     *
     *  * `public` ist Default und wird meist weggelassen.
     *
     *  * Sichtbarkeiten:
     *      - `private` - anders als in Java wirklich nur innerhalb der aktuellen Klasse
     *      - `protected` - genauso wie `private` und zusätzlich in Subklassen
     *      - `internal` - für jeden im selben Module (Compile-Einheit)
     *      - `public` - für alle
     *
     */
    "Eine einfache Klasse" {


        class Rechteck {

            var breite = 0
            var hoehe = 0

            fun berechneFlaeche() = breite * hoehe

        }


        val r = Rechteck()
        r.breite = 3
        r.hoehe = 4


        val flaeche = r.berechneFlaeche()

        r.breite shouldBe 3
        r.hoehe shouldBe 4
        flaeche shouldBe 12
    }


    /**
     * ## Sekundäre Konstruktoren
     *
     * * Es gibt *immer* nur genau einen primären Konstruktor.
     *
     * * Man darf weitere Sekundäre Konstrukturen hinzufügen
     *
     *   - Grund:
     *     - weitere Konstruktoren für mehr Komfort
     *     - Kapselung des primären Konstruktors
     *
     *   - Mit `constructor(...)` im Deklarationsblock
     *
     *   - Die sekundären Konstruktoren müssen den primären Konstruktor oder einen anderen sekundären Konstruktor aufrufen (mit `: this(...)`)
     *
     *   - Ein Code-Block darf angegeben werden.
     */
    "Mehrere Konstruktoren" {

        class NumericString private constructor(val stringRepresentation: String) {

            constructor(ganzeZahl: Int) : this(ganzeZahl.toString())

            constructor(ganzeZahl: Double) : this(ganzeZahl.toString())

            constructor(p: Int, q: Int) : this("$p/$q") {
                if (q == 0)
                    throw IllegalArgumentException("Important: q must not be 0!")
            }
        }


        NumericString(42).stringRepresentation shouldBe "42"
        NumericString(47.11).stringRepresentation shouldBe "47.11"
        NumericString(3, 4).stringRepresentation shouldBe "3/4"
        val exception = shouldThrow1<IllegalArgumentException> {
            NumericString(3, 0)
        }
        exception.message shouldContain "must not be 0"
    }


    /**
     * ## `init`-Blöcke werden bei jeder Instanzerzeugung aufgerufen.
     *
     * Wenn man möchte, dass mit dem primären Konstruktor extra Code
     * ausgeführt wird, nimmt man einen `init`-Block.
     */
    "Wenn vorhanden, wird mit dem primären Konstruktor auch ein init-Block ausgeführt" {


        class Betrag(b: Double) {

            val betrag: Double

            init {
                betrag = round(b * 100.0) / 100.0
            }

        }

        Betrag(47.11).betrag shouldBe 47.11
        Betrag(47.1134).betrag shouldBe 47.11
        Betrag(47.115).betrag shouldBe 47.12
        Betrag(47.1163).betrag shouldBe 47.12
    }


    "Berechneter Getter" {

        class Rechteck(val breite: Int, val hoehe: Int) {

            val flaeche: Int
                get() = breite * hoehe

        }

        val r = Rechteck(3, 4)

        r.flaeche shouldBe 12
    }

    /**
     * ### Setter
     *
     * * Bei `var`-Feldern kann auch ein Setter definiert werden.
     *
     * * Mit `set(v) { ... }` hinter der Feld-Deklaration.
     *
     */
    "Codierte Getter und Setter" {

        class Winkel(var radiant: Double) {
            var grad: Double
                get() = radiant * 180.0 / PI
                set(gradValue) {
                    radiant = gradValue / 180.0 * PI
                }
        }

        val w = Winkel(PI)


        w.grad = 45.0


        w.radiant shouldBe (PI / 4.0)
    }

    /**
     * Innere Klassen die implizit auf das äussere Objekt zugreifen sollen
     * müssen mit Inner definiert werden
     */
    "Innere Klasse" {
        class Outer(val a: Int) {
            inner class Inner(val b: Int) {
                fun accessOuter(): Int {
                    return a + b
                }
            }

            val inner = Inner(2)
        }

        val o = Outer(1)
        o.inner.accessOuter() shouldBe 3
    }

    /**
     * ## Vererbung
     *
     * - Nur erlaubt, wenn Oberklasse als `open` deklariert ist.
     * - Nach dem `:` wird ein Konstruktur der Oberklasse aufgerufen.
     */
    "Vererbung" {

        open class Name(val vorname: String, val name: String) {
            fun notOverridable() = Unit
            open fun overridable1() = Unit
            open fun overridable2() = Unit

        }

        open class Ansprache(val anrede: String, vorname: String, name: String) : Name(vorname, name) {
            override fun overridable1() {

            }

            //Nicht mehr overridable
            final override fun overridable2() {
                super.overridable2()
            }
        }


        val a = Ansprache("Lady", "Ada", "Lovelace")
        a.anrede shouldBe "Lady"
        a.vorname shouldBe "Ada"
        a.name shouldBe "Lovelace"
    }


    /**
     * ## Interfaces
     *
     * * Sehr ähnlich wie in Java
     * * `override` muss angegeben werden. (Gilt auch bei Vererbung)
     *
     */
    "Interfaces" {

        // Deklaration des Interfaces `HelloSayer` unterhalb dieser Funktion

        class Moin : HelloSayer {
            override fun sayHello() = println("Moin Moin!")
        }

        Moin().sayHello()
    }


    /**
     * ## Objekte
     *
     * Soll es von einer Klasse nur ein Objekt geben (Singleton) kann man dieses gleich per `object` definieren
     * * Singleton-Objekte beginnen wie Klassen mit einem großen Buchstaben
     * * Keine Konstruktorparameter möglich, da das Objekt automatisch erzeugt wird (`init` ist möglich)
     * * Objekte können auch local angelegt werden: entspricht anonymer innerer Klasse
     */
    "Singleton Objekte()" {
        // Deklaration des Objekts `MoinMoin` unterhalb dieser Funktion

        MoinMoin.sayHello()

        // lokales Objekt welches ein Interface implementiert
        val localMoin = object : HelloSayer {
            override fun sayHello() = println("Moin Moin!")
        }

        localMoin.sayHello()
    }


    /**
     * ### Companion Objects
     *
     * Utility-Funktionen, Konstanten und globale Variablen
     * einer bestimmten Klasse zuordnen kann man sie
     * in einem `companion object` stecken.
     *
     * * Das Companion Objekt ist wird nach dem Laden der dazugehörigen Klasse initialisiert.
     *
     * * Felder und Funktionen können über den Namen der umgebenden Klasse
     *   adressiert werden (wie `static`'s in Java).
     */
    "Ein Companion als Factory" {


        // Anmerkung die Beispielklasse ist unterhalb definiert,
        // weil lokale Klassen in Funktionen keine Companions haben dürfen.


        val a = NummeriertesObjekt.next()
        val b = NummeriertesObjekt.next()
        val c = NummeriertesObjekt.next()
        val d = NummeriertesObjekt.DIESE_ZAHL_SCHON_WIEDER


        a.nr shouldBe 0
        b.nr shouldBe 1
        c.nr shouldBe 2
        d shouldBe 42
    }


    /**
     * ### Enums
     *
     * Anders als in Java heißt es hier `enum class` an Stelle von `enum`.
     *
     */
    "Ein einfaches Enum" {

        // Deklaration unten

        Richtung.LINKS shouldNotBe Richtung.RECHTS
        Richtung.values() shouldHaveSize 2

    }

})

interface HelloSayer {
    fun sayHello()
}

object MoinMoin : HelloSayer {
    override fun sayHello() = println("Moin Moin!")
}

class NummeriertesObjekt private constructor(val nr: Int) {

    companion object {
        var nextNr = 0

        fun next() = NummeriertesObjekt(nextNr++)

        val DIESE_ZAHL_SCHON_WIEDER = 42
    }
}

enum class Richtung { LINKS, RECHTS }
