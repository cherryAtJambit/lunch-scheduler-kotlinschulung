package de.e2.misc

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SealedClassesADT : StringSpec({
    "Überprüfung auf Vollständigeit" {
        val shape: Shape = Shape.Rect(1,2)
        val result = when (shape) {
            is Shape.Rect -> "rect ${shape.a} ${shape.b}"
            is Shape.Circle -> "circle ${shape.r}"
            Shape.Nothing -> "nothing"
        }

        result shouldBe "rect 1 2"
    }
})

sealed class Shape {
    class Rect(val a: Int, val b: Int) : Shape()
    class Circle(val r: Int) : Shape()
    object Nothing : Shape()
}