package de.e2.misc

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.*
import java.sql.Date as SqlDate
import kotlin.text.toLowerCase as inKleinbuchstaben


typealias Alter = Int

inline class Name(val value: String)

class TypeAlias_Inline_Class : StringSpec({
    "Typaliase geben Typen zusätzliche Namen" {
        val alter: Alter = 10
        val zahl: Int = alter

        alter shouldBe zahl
    }

    "Import Alias fuer gleichnamige Klassen" {
        val javaDate = Date()
        val sqlDate = SqlDate(javaDate.time)

        sqlDate.time shouldBe  javaDate.time
    }

    "Import Alias fuer 'schoenere' Namen" {
        "ABC".inKleinbuchstaben() shouldBe "abc"
    }

    "Inline Klassen erzeugen neue Typen" {
        val name: Name = Name("Rene")

        //Compile Error
//        val str : String = name
        val str = "Rene"

        name shouldNotBe str
    }
})