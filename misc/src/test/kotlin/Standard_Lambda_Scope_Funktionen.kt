package de.e2.misc

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.io.File
import java.util.*

/**
 * ## Hilfreiche Standardfunktionen
 * * die folgenden Funktionen erlauben idiomatischen Kotlin-Code
 * * sie vermeiden häufig Hilfsvariablen und komplexe Ausdrücke
 *
 * Für die Auswahl siehe:
 * ['Mastering Kotlin standard functions: run, with, let, also and apply'](https://medium.com/@elye.project/mastering-kotlin-standard-functions-run-with-let-also-and-apply-9cd334b0ef84)
 */
class Standard_Lambda_Scope_Funktionen : StringSpec({
    data class Address(val city: String, val zip: String)
    class Person(val firstName: String, val surname: String, val email: String, val address: Address? = null)

    val person1 = Person("Rene", "Preissel", "rp@etosquare.de", Address("Hamburg", "22391"))
    val person2 = Person("Anke", "Preissel", "anke@etosquare.de")


    /**
     * `with` kann direkt als Body-Block benutzt werden um den Zugriff auf einen Parameter zu vereinfachen
     */
    "Nochmal with" {

        fun createAddressLabel(person: Person): String = with(person) {
            """
            $firstName $surname
            ${address?.zip} ${address?.city}
            """.trimIndent()
        }

        println(createAddressLabel(person1))
    }

    /**
     * `let` erlaubt es den Receiver umzuwandeln, genauso wie `map` bei Collections
     */
    "let verhält sich für normale Variablen wie map für Collections" {

        val personList = listOf(person1, person2)
        val fullnameList = personList.map { "${it.firstName} ${it.surname}" }
        fullnameList[0] shouldBe "Rene Preissel"
        fullnameList[1] shouldBe "Anke Preissel"

        //Mit let geht das auch für ein Objekt
        val fullname = person1.let { "${it.firstName} ${it.surname}" }
        fullname shouldBe "Rene Preissel"
    }

    "let zusammen mit dem Fragezeichen-Operator ist noch mächtiger" {
        val addressString1 = person1.address?.let { "${it.zip} ${it.city}" }
        addressString1 shouldBe "22391 Hamburg"

        val addressString2 = person2.address?.let { "${it.zip} ${it.city}" }
        addressString2.shouldBeNull()

        val addressString3 = person2.address?.let { "${it.zip} ${it.city}" } ?: "unbekannt"
        addressString3 shouldBe "unbekannt"
    }

    /**
     * `run` ist wie `with`, nur definiert als Extension Funktion
     * * wenn der Receiver allerdings `null` sein kann, ist `run` idiomatischer
     */
    "run verhält sich wie with" {
        val addressLabel1 = with(person1) {
            """
            $firstName $surname
            ${address?.zip} ${address?.city}
            """.trimIndent()
        }

        val addressLabel2 = person1.run {
            """
            $firstName $surname
            ${address?.zip} ${address?.city}
            """.trimIndent()
        }

        addressLabel1 shouldBe addressLabel2

        //Bei Null-Werten ist run idiomatischer als with

        val addressString = person1.address?.run { "${zip} ${city}" }
        addressString shouldBe "22391 Hamburg"
    }

    /**
     * 'apply' wird benutzt um die Initialisierung zu vereinfachen, wenn kein passender Konstruktor vorhanden ist
     * Ansonsten ist `apply` fast so wie `run` und `with`, nur das der Receiver zurückgegeben wird.
     */
    "apply vereinfacht die Initialisierung" {
        val weekdayMap = HashMap<String, String>().apply {
            put("Mo", "Monday")
            put("Tu", "Tuesday")
        }

        weekdayMap shouldContainKey "Mo"
        weekdayMap shouldContainKey "Tu"
    }

    /**
     * 'also' ist wie `apply` nur wird dabei der `this`-Zeiger nicht redefiniert, sondern ein normaler Parameter übergeben
     */
    "also vereinfacht die Initialisierung auch" {
        val weekdayMap = HashMap<String, String>().also { map ->
            map.put("Mo", "Monday")
            map.put("Tu", "Tuesday")
        }
        weekdayMap shouldContainKey "Mo"
        weekdayMap shouldContainKey "Tu"
    }

    /**
     * 'takeIf' in Kombination mit `?.let`/ `?.run` erlaubt das kompakte Schreiben von Bedingungen
     */
    "takeIf ersetzt ein normales if" {

        val onlyAddressesInHamburgString1 = if (person1.address?.zip?.startsWith("22") ?: false) {
            person1.address?.run { "$zip $city" }
        } else {
            "not in hamburg"
        }

        onlyAddressesInHamburgString1 shouldBe "22391 Hamburg"

        val onlyAddressesInHamburgString2 = person1.address
            ?.takeIf { it.zip.startsWith("22") }
            ?.run { "$zip $city" } ?: "not in hamburg"

        onlyAddressesInHamburgString2 shouldBe "22391 Hamburg"
    }

    /**
     * 'use' ist der Ersatz für `try-with-resources` aus Java
     * * `use` ist eine Extension-Funktion für ein `Closable`
     */
    "use schliest Resourcen nach der Benutzung" {

        File("address.txt").writer().use {
            it.write(person1.address?.run { "$zip $city" } ?: "unbekannt")
        }
    }

    "isBlank, ifBlank, ifEmpty, isEmpty, orEmpty für Strings" {
        "".assertSoftly {
            it.isBlank().shouldBeTrue()
            it.ifBlank { "was blank" } shouldBe "was blank"

            it.isEmpty().shouldBeTrue()
            it.ifEmpty { "was empty" } shouldBe "was empty"
        }

        " ".assertSoftly {
            it.isBlank().shouldBeTrue()
            it.ifBlank { "was blank" } shouldBe "was blank"

            it.isEmpty().shouldBeFalse()
            it.ifEmpty { "was empty" } shouldBe " "
        }

        (null as String?).assertSoftly {
            it.isNullOrBlank().shouldBeTrue()
            it.orEmpty().ifBlank { "was blank or null" } shouldBe "was blank or null"

            it.isNullOrEmpty().shouldBeTrue()
            it.orEmpty().ifEmpty { "was empty or null" } shouldBe "was empty or null"
        }
    }
})


