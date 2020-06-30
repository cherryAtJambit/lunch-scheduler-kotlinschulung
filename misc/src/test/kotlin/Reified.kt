package de.e2.misc

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.StringSpec
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*


/**
 * ##  Inline Reified erlaubt den Zugriff auf geenrische Parameter zur Laufzeit
 * * Type-Erasure macht den Zugriff auf generische Parameter zur Laufzeit normalerweise unmöglich
 * * Durch Inlineing und das Schlüsselwort `reified` wird es möglich
 */
class Reified : StringSpec({

    "01 Beispiel mit Arrays" {

        val numbers = Arrays.asList(1, 2, 3)
        val intArray = numbers.toTypedArray()

        assertTrue(intArray is Array<Int>)
    }

    "Beispiel mit Jackson" {

        val json = """
                   {
                        "vorname": "Rene",
                        "nachname": "Preissel"
                   }
                   """

        val objectMapper = jacksonObjectMapper()


        //Typ erkannt anhand des Typs der Variablen
        val person1: Person = objectMapper.myReadValue(json)
        println(person1)

        //Typ explizit als generischen Parameter übergeben
        val person2 = objectMapper.myReadValue<Person>(json)
        println(person2)

        fun doSomethingWithPerson(person: Person) {
            println(person)
        }

        //Typ erkannt anahand des Methoden-Parameters
        doSomethingWithPerson(objectMapper.myReadValue(json))
    }
})

data class Person(val vorname: String, val nachname: String)

//Reified ermöglich den Zugriff auf die Klasses des generischen Parameters in der Funktion
inline fun <reified T> ObjectMapper.myReadValue(json: String): T = readValue(json, T::class.java)





