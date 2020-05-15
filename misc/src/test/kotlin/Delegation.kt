package de.e2.misc

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.math.PI
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * _Delegated Properties_ erlauben es die Getter und Setter von Properties über ein anderes Objekt zu delegieren und
 * dabei Funktionalität hinzuzufügen. Das Schlüsselwort ist `by`.
 */
class Delegation : StringSpec({

    "Das Schlüsselwort by implementiert Delegation Properties" {
        class Circle(val x: Double, val y: Double, val radius: Double) {
            val area: Double by lazy {
                println("Init Area")
                PI * radius * radius
            }
        }

        val c = Circle(1.0, 1.0, 1.0)
        println("Init Circle")
        println(c.area)
    }

    "Neben Lazy gibt es auch observable und vetoable in der StdLib" {
        fun <T> logChanges(property: KProperty<*>, oldValue: T, newValue: T) {
            println("Changed ${property.name} from $oldValue to $newValue")
        }

        class Circle(x: Double, y: Double, radius: Double) {
            var x by Delegates.observable(x) { _, old, new ->
                println("Changed x to $new")
            }

            var y by Delegates.observable(y, ::logChanges)
            var radius by Delegates.observable(radius, ::logChanges)
        }

        val c = Circle(1.0, 1.0, 1.0)
        c.x = 5.0
        c.y = 10.0
    }

    /**
     * Delegation Objekte müssen spezielle Operatoren `getValue` und  ggf. `setValue` definieren
     */
    "Eigene Delegates können geschrieben werden" {

        class Tracer<T>(var value: T) {
            operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
                println("In ${thisRef?.javaClass?.simpleName} read - ${property.name} : $value")
                return value
            }

            operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
                println("In ${thisRef?.javaClass?.simpleName} write - ${property.name} : $value -> $newValue")
                value = newValue
            }

        }

        class Circle(x: Double, y: Double, radius: Double) {
            var x by Tracer(x)
            var y by Tracer(y)
            var radius by Tracer(radius)
        }

        val c = Circle(1.0, 1.0, 1.0)
        println(c.x)
        c.y = 10.0
    }


    "Maps implementieren auch die getValue und setValue Operatoren" {

        class Person(data: Map<String, Any?>) {
            val firstname: String by data
            val surname: String by data
            val eMail: String? by data
        }

        val data = mapOf("firstname" to "Rene", "surname" to "Preißel")
        val person = Person(data)

        person.firstname shouldBe "Rene"
    }

    "Es können auch alle Methoden eines Interfaces delegiert werden" {

        class Person(data: MutableMap<String, Any?>) : MutableMap<String, Any?> by data {
            var firstname: String by data
            var surname: String by data
            var eMail: String? by data
        }

        val person = Person(
            mutableMapOf(
                "firstname" to "Rene",
                "surname" to "Preißel"
            )
        )

        person shouldHaveSize 2

        person["eMail"] = "rp@etosquare.de"
        person.eMail shouldBe "rp@etosquare.de"

        person shouldHaveSize 3
    }
})


