package de.e2.kson.v3

class Json {
    private val content: MutableMap<String, String> = mutableMapOf()

    operator fun String.plusAssign(other: Any) {
        content[this] = other.toString()
    }

    operator fun String.plusAssign(other: String) {
        content[this] = """"$other""""
    }

    operator fun String.plusAssign(body: Json.() -> Unit) {
        content[this] = Json().apply(body).toString()
    }

    override fun toString() =
        content.toList().map { """ "${it.first}" : ${it.second} """ }.joinToString(prefix = "{", postfix = "}")

}

fun json(body: Json.() -> Unit) =
    Json().apply(body).toString()


fun main() {
    val json = json {
        "name" += "Rene"
        "adresse" += {
            "stadt" += "Hamburg"
            "plz" += 22391
        }
    }

    println(json) //{ "name" : "Rene" ,  "adresse" : { "stadt" : "Hamburg" ,  "plz" : 22391 } }
}