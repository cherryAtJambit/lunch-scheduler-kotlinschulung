package de.e2.misc

import io.kotest.core.spec.style.StringSpec

class Inner
fun innerFun(body: Inner.() -> Unit) {

}

inline fun innerFunInline(body: Inner.() -> Unit) {

}


class ExplizitesReturn : StringSpec({
    "Explizites Return" {
        fun aFunction() {
            innerFun {
                return@innerFun // explizites Return notwendig
            }

            innerFunInline {
                return //verlässt aFunction
            }

            innerFunInline {
                return@innerFunInline //verlässt innerFunInline
            }

            for (i in 1..10) {
                return //verlässt aFunction
            }

            (1..10).forEach {
                return //verlässt aFunction
            }

            (1..10).forEach {
                return@forEach //verlässt forEach
            }
        }
    }
})