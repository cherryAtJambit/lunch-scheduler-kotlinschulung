package de.e2.misc

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.Month

class Infix_Operatoren : StringSpec({
    "Infix Funktionen erhöhen die Lesbarkeit" {
        val daysOhneInfix = mapOf("Mo".to("Monday"), "Tu".to("Tuesday"))

        val daysMitInfix = mapOf("Mo" to "Monday", "Tu" to "Tuesday")

        daysOhneInfix shouldBe daysMitInfix
    }

    "Eigene Infix Funktionen eigenen sich besonders im algebraischen Umfeld" {
        class Circle(val x: Double, val y: Double, val radius: Double) {
            infix fun intersects(other: Circle): Boolean {
                val distanceX = this.x - other.x
                val distanceY = this.y - other.y
                val radiusSum = this.radius + other.radius
                return distanceX * distanceX + distanceY * distanceY <= radiusSum * radiusSum
            }
        }

        val c1 = Circle(x = 100.0, y = 100.0, radius = 50.0)
        val c2 = Circle(x = 75.0, y = 75.0, radius = 5.0)

        (c1 intersects c2) shouldBe true
    }

    "Eigenen Operator definieren" {
        class Circle(val x: Double, val y: Double, val radius: Double) {
            infix fun intersects(other: Circle): Boolean {
                val distanceX = this.x - other.x
                val distanceY = this.y - other.y
                val radiusSum = this.radius + other.radius
                return distanceX * distanceX + distanceY * distanceY <= radiusSum * radiusSum
            }

            operator fun rem(other: Circle): Boolean = intersects(other)
        }

        val c1 = Circle(x = 100.0, y = 100.0, radius = 50.0)
        val c2 = Circle(x = 75.0, y = 75.0, radius = 5.0)
        (c1 % c2) shouldBe true
    }

    "Vergleichs-Operatoren redefinieren" {
        class Circle(val x: Double, val y: Double, val radius: Double) : Comparable<Circle> {
            override operator fun compareTo(other: Circle): Int = (this.radius - other.radius).let {
                when {
                    it < -0.0001 -> -1
                    it > 0.0001 -> +1
                    else -> 0
                }
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Circle

                if (compareTo(other) != 0) return false

                return true
            }

            override fun hashCode(): Int {
                return radius.hashCode()
            }
        }

        val c1 = Circle(x = 100.0, y = 100.0, radius = 50.0)
        val c2 = Circle(x = 75.0, y = 75.0, radius = 5.0)

        (c1 == c2) shouldBe false
        (c1 > c2) shouldBe true
    }

    "Destructuring operator" {
        val date = LocalDate.of(2020, Month.JULY, 1)

        operator fun LocalDate.component1() = year
        operator fun LocalDate.component2() = month
        operator fun LocalDate.component3() = dayOfMonth

        val (y, _, d) = date

        y shouldBe 2020
        d shouldBe 1
    }
})