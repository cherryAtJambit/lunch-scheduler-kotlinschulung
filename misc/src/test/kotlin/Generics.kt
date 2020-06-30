package de.e2.misc

import io.kotest.core.spec.style.StringSpec
import java.io.Serializable

abstract class Tier
open class Hund : Tier()
class Pudel : Hund()

class Generics : StringSpec({
    "Invarianz ist Standard" {
        class Container<T : Tier>() {
            fun returnsT(): T? {
                return null
            }

            fun receivesT(t: T?) {
            }
        }

        val hundContainer = Container<Hund>()

        //Compile Fehler
//        val tierContainer : Container<Tier> = hundContainer
        //Compile Fehler
//        val pudelContainer : Container<Pudel> = hundContainer

    }

    /**
     * Kovarianz entspricht dem Verhalten eines Producers: output/out
     * Entspricht `extends` in Java
     */
    "Kovarianz oder out" {
        class Producer<out T : Tier>() {
            fun returnsT(): T? {
                return null
            }

            // Compile Error ist nicht erlaubt
//            fun receivesT(t: T?) {
//            }
        }

        val hundProducer = Producer<Hund>()

        val tierProducer: Producer<Tier> = hundProducer
        //Compile Fehler: Der Producer kann auch was anderes als Pudles erzeugen
//        val pudelProducer : Producer<Pudel> = hundProducer

    }

    /**
     * Kontravarianz entspricht dem Verhalten eines Consumers: input/in
     * Entspricht `super` in Java
     */
    "Kontravarianz oder in" {
        class Consumer<in T : Tier>() {
            // Compile Error ist nicht erlaubt
//            fun returnsT(): T? {
//              return null
//            }

            fun receivesT(t: T?) {
            }
        }

        val hundConsumer = Consumer<Hund>()

        //Compile Fehler: Der HundConsumer erwartet nur Hunde und kann mit anderen Tieren nicht umgehen
//        val tierConsumer : Consumer<Tier> = hundConsumer

        val pudelConsumer: Consumer<Pudel> = hundConsumer
    }

    /**
     * Wenn das Verhalten bei der Dekleration nicht eindeutig ist, kann man es bei
     * der Benutzung spezifizieren. Entpsricht dem Java-Verhalten
     */
    "Varianz bei der Benutzung - Use-Site-Variance" {
        class Container<T : Tier>() {
            fun returnsT(): T? {
                return null
            }

            fun receivesT(t: T?) {
            }
        }

        fun copy(von: Container<out Hund>, nach: Container<in Hund>) {
            val hund1 = von.returnsT()
            nach.receivesT(hund1)

            //Liefert Any? zurück
            val hund2 = nach.returnsT()

            //Compile Fehler
//            von.receivesT(hund2)
        }

        copy(Container<Pudel>(), Container<Tier>())
    }

    /**
     * Wenn der generische Typ keine Rolle spielt, dann kann man Star-Projektion benutzen.
     * Entspricht dem Wildcard ? in Java.
     */
    "Star Projection" {
        class Container<T : Tier>() {
            fun returnsT(): T? {
                return null
            }

            fun receivesT(t: T) {
            }
        }

        class Producer<out T : Tier>() {
            fun returnsT(): T? {
                return null
            }
        }

        class Consumer<in T : Tier>() {
            fun receivesT(t: T?) {
            }
        }


        fun tuwasMitContainer(container: Container<*>) {
            val tier: Tier? = container.returnsT()

            //Compile Fehler
            //container.receivesT(..)
        }
        tuwasMitContainer(Container<Tier>())
        tuwasMitContainer(Container<Hund>())
        tuwasMitContainer(Container<Pudel>())

        fun tuwasMitProducer(producer: Producer<*>) {
            val tier: Tier? = producer.returnsT()
        }
        tuwasMitProducer(Producer<Tier>())
        tuwasMitProducer(Producer<Hund>())
        tuwasMitProducer(Producer<Pudel>())


        fun tuwasMitConsumer(consumer: Consumer<*>) {
            //Compile Error
//            consumer.receivesT(...)
        }
        tuwasMitConsumer(Consumer<Tier>())
        tuwasMitConsumer(Consumer<Hund>())
        tuwasMitConsumer(Consumer<Pudel>())

    }

    "Generische Funktionen mit Constraints" {
        fun <T : Comparable<T>> sort(list: List<T>) {}

        sort(listOf<Int>())

        //Compile Error
//        sort(listOf<Any>())

        fun <T> sortSerializable(list: List<T>) where T : Comparable<T>, T : Serializable {}

        class Data : Comparable<Data> {
            override fun compareTo(other: Data): Int = 0
        }

        //Compile Error
//        sortSerializable(listOf<Data>())
    }
})