package de.e2.springreactive.annotations

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest
@WithMockUser
class RestServiceMvcTest(@Autowired val webTestClient: WebTestClient) {

    @MockkBean
    private lateinit var adresseRepository: AdresseRepository

    @Test
    fun testFindAll() {
        every { adresseRepository.findAll() } returns flowOf(
            Adresse("s", "h", "p", "s", 0),
            Adresse("s", "h", "p", "s", 1)
        )

        webTestClient.get().uri("/api/adresse/").accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("\$.[0].id").isEqualTo(0)
            .jsonPath("\$.[0].strasse").isEqualTo("s")
            .jsonPath("\$.[1].id").isEqualTo(1)
            .jsonPath("\$.[1].strasse").isEqualTo("s");
    }
}