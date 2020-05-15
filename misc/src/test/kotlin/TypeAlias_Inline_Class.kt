package de.e2.misc

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDate as BetterDate


typealias Alter = Int


inline class Name(val value: String)

class TypeAlias_Inline_Class : StringSpec({
    "Typaliase geben Typen zusätzliche Namen" {
        val alter: Alter = 10
        val zahl: Int = alter

        alter shouldBe zahl
    }

    "Imports mit Alias" {
        val date = BetterDate.now()

        date::class shouldBe java.time.LocalDate::class
    }

    "Inline Klassen erzeugen neue Typen" {
        val name: Name = Name("Rene")

        //Compile Error
//        val str : String = name
        val str = "Rene"

        name shouldNotBe str
    }
})