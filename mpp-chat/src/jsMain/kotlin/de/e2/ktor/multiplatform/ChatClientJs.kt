package de.e2.ktor.multiplatform

import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.url.URLSearchParams
import kotlin.browser.document
import kotlin.browser.window

//<editor-fold desc="Helper function" defaultstate="collapsed">
inline fun <reified T : Element> elementById(id: String): T =
    document.getElementById(id) as? T ?: throw IllegalStateException("Element $id not found or wrong type")
//</editor-fold>

fun main() {
    document.addEventListener("DOMContentLoaded", {
        val newMessage: HTMLInputElement = elementById("newMessage")
        val messages: HTMLTextAreaElement = elementById("messages")
        val sendButton: HTMLButtonElement = elementById("submit")

        val searchParameter = URLSearchParams(window.location.search)
        val nickName = searchParameter.get("nickname") ?: "Javascript"

        val chatClient = joinChat(nickName) { msg ->
            messages.value = "${messages.value}$msg\n"
        }

        sendButton.addEventListener("click", { event ->
            chatClient.send(newMessage.value)
            event.preventDefault()
        })

    })
}