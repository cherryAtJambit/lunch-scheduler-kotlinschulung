package de.e2.kson.v2

data class Lunch(var host: Host? = null, var restaurant: Restaurant? = null) {

    fun host(body: Host.() -> Unit) {
        host = Host().apply(body)
    }

    fun restaurant(body: Restaurant.() -> Unit) {
        restaurant = Restaurant().apply(body)
    }
}

data class Host(var name: String? = null) {
}

data class Restaurant(var name: String? = null) {
}

fun lunch(body: Lunch.() -> Unit): Lunch {
    val lunch = Lunch()
    lunch.body()
    return lunch
}

fun main() {
    val lunch = lunch {
        host {
            name = "Schlunz"
        }
        restaurant {
            name = "Mc Dreck"
        }
    }

    println(lunch)
}