package de.e2.kson.v2

class JsonBuilder {
    private val content: MutableMap<String, String> = mutableMapOf()

    infix fun String.assign(other: Any) {
        content[this] = other.toString()
    }

    infix fun String.assign(other: String) {
        content[this] = """"$other""""
    }

    infix fun String.assign(body: JsonBuilder.() -> Unit) {
        content[this] = JsonBuilder().apply(body).toString()
    }

    override fun toString() =
        content.toList().map { """ "${it.first}" : ${it.second} """ }.joinToString(prefix = "{", postfix = "}")

}

fun json(body: JsonBuilder.() -> Unit) =
    JsonBuilder().apply(body).toString()


fun main() {
    val json = json {
        "name" assign "Rene"
        "adresse" assign {
            "stadt" assign "Hamburg"
            "plz" assign 22391
        }
    }

    println(json) //{ "name" : "Rene" ,  "adresse" : { "stadt" : "Hamburg" ,  "plz" : 22391 } }
}