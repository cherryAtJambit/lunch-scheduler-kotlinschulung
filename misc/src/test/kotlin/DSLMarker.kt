package de.e2.misc

import io.kotest.core.spec.style.StringSpec
import org.junit.jupiter.api.Test

/**
 * ## DSLMarker ermöglicht typsichere Builder ohne das Ebenen übersprungen werden
 */
class DSLMarker : StringSpec({


    "Probleme ohne Einsatz von DSLMarker" {

        class Inner

        class Outer {
            fun inner(block: Inner.() -> Unit) {}
        }

        fun outer(block: Outer.() -> Unit) {}


        outer {

            inner {
                //ERROR: Hier wird die Funktion beim Outer-This aufgerufen
                inner {

                }
            }
        }
    }



    "DSLMarker nimmt immer nur den innersten This-Zeiger implizit" {

        @MyDSL
        class Inner

        @MyDSL
        class Outer {
            fun inner(block: Inner.() -> Unit) {}
        }

        fun outer(block: Outer.() -> Unit) {}


        outer {

            inner {
                // Jetzt gibt es einen Compile-Error
//              inner {
//
//              }

                // Explizit geht es weiterhin
                this@outer.inner {

                }
            }
        }
    }
})

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class MyDSL
