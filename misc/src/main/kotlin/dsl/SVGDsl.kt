package de.e2.misc.dsl


@DslMarker
annotation class SVGDslMarker

data class RGB(val r: Int, val g: Int, val b: Int) {
    override fun toString(): String = "rgb($r,$g,$b)"
}

@SVGDslMarker
abstract class SVGElement {
    var fill: RGB? = null
    var stroke: RGB? = null
    var strokeWidth: Int? = null

    val style: String
        get() {
            val styles = mapOf(
                "fill" to fill,
                "stroke" to stroke,
                "stroke-width" to strokeWidth
            ).filterValues { it != null }
                .map { """ ${it.key}="${it.value}" """.trim() }
                .joinToString(" ")

            return styles.takeIf(String::isNotEmpty) ?: ""
        }

    abstract fun generate(): String
}

@SVGDslMarker
class SVG {
    var width = 100
    var height = 100
    val elements = mutableListOf<SVGElement>()

    fun generate(): String =
        """
            <svg width="$width" height="$height">
                ${elements.map(SVGElement::generate).joinToString("\n")}
            </svg>
        """.trimIndent()
}

inline fun svg(body: SVG.() -> Unit): String {
    return SVG().apply(body).generate()
}

class Rect(var width: Int, var height: Int) : SVGElement() {
    override fun generate(): String =
        """
            <rect width="$width" height="$height" $style>
            </rect>
        """.trimIndent()
}

inline fun SVG.rect(width: Int = 10, height: Int = 10, body: Rect.() -> Unit = {}) {
    elements += Rect(width, height).apply(body)
}


class Circle(var cx: Int, var cy: Int, var r: Int) : SVGElement() {
    override fun generate(): String =
        """
            <circle cx="$cx" cy="$cy" r="$r" $style>
            </circle>
        """.trimIndent()
}

inline fun SVG.circle(cx: Int = 10, cy: Int = 10, r: Int = 10, body: Circle.() -> Unit = {}) {
    elements += Circle(cx, cy, r).apply(body)
}

