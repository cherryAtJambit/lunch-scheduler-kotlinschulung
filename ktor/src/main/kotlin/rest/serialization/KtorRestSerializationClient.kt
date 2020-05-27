package de.e2.ktor.rest.serialization

import io.ktor.client.HttpClient
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

suspend fun main() {
    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }

        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
            url.host = "localhost"
            url.port = 8080
        }
    }

    val response = client.post<MyResponse>(
        path = "rest", body = MyRequest("abc")
    )
    println(response)
}


