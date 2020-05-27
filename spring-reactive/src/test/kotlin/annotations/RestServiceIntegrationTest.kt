package de.e2.springreactive.annotations

import de.e2.springreactive.annotations.Adresse
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser
class RestServiceIntegrationTest(@Autowired val restTemplate: TestRestTemplate) {
    @Test
    fun testFindAll() {
        val result= restTemplate
            .withBasicAuth("user","user")
            .getForEntity<Array<Adresse>>("/api/adresse/")
        result.statusCode shouldBe HttpStatus.OK
        val body = result.body

        body.shouldNotBeNull()
        body.size shouldBe 10
    }
}