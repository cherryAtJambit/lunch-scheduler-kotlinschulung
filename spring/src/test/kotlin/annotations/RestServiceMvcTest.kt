package de.e2.spring.annotations

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest
@WithMockUser
class RestServiceMvcTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var adresseRepository: AdresseRepository

    @Test
    fun testFindAll() {
        every { adresseRepository.findAll() } returns listOf(
            Adresse("s", "h", "p", "s", 0),
            Adresse("s", "h", "p", "s", 1)
        )

        mockMvc.get("/api/adresse/") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("\$.[0].id") {
                value(0)
            }
            jsonPath("\$.[0].strasse").value("s")
            jsonPath("\$.[1].id").value(1)
            jsonPath("\$.[1].strasse").value("s")
        }
    }
}