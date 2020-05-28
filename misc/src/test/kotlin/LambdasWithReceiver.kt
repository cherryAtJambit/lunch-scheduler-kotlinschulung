package p04_extensions_lambdas

import io.kotest.core.spec.style.StringSpec

/**
 * ## Lambdas with Receiver
 * * Lambdas mit eigenen This-Zeiger
 */
class LambdasWithReceiver : StringSpec({

    "Wiederholung einfache Lambdas" {

        //Funktion mit Lambda als Parameter
        fun times(times: Int, action: (Int) -> Unit) {
            for (index in 0 until times) {
                action(index)
            }
        }

        times(5) { index ->
            println(index)
        }
    }

    /**
     * Lambdas with Receiver werden wie anonyme Extension-Funktionen definiert: `<Type>.(...) -> <Result>`
     */
    "Lambdas mit eigenen This-Zeiger" {

        class Person(val firstName: String, val surname: String, val email: String)

        // Person.() -> Unit definiert eine Lambda das nur auf einer Person aufgerufen werden darf
        fun withPerson(receiver: Person, block: Person.() -> Unit) {

            //Bei Aufruf mus das Objekt vorangestellt werden
            receiver.block() //Alternativ: block(receiver)

        }

        val rene = Person("Rene", "Preissel", "rp@etosquare.de")

        withPerson(rene) {
            //this Zeiger ist eine Person
            println("$firstName $surname $email")
        }
    }

    /**
     * Bei Lambdas with Receiver gibt es oft mehrere `this`-Zeiger im selben Kontext.
     * * einerseits den redefinierten `this`-Zeiger
     * * andererseits den umschliessenden `this`-Zeiger
     * * mit `this@...' kann explizit einer angesprochen werden
     * * ansosnten gewinnt der innere vor dem umschliessenden
     */
    "This-Zeiger explizit ansprechen" {

        class Person(val firstName: String, val surname: String, val email: String)

        class PersonService {
            fun doWithPerson(person: Person) {

                with(person) {
                    //hier gibt es zwei this-Zeiger
                    val personThis: Person = this@with
                    val serviceThis: PersonService = this@PersonService
                }

            }
        }

        PersonService().doWithPerson(Person("Rene", "Preissel", "rp@etosquare.de"))
    }
})


