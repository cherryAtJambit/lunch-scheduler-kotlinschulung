package de.e2.koin

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Rule
import org.junit.Test
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock

class MockingWithKoinJunitTest : KoinTest {
    class PersonDao {
        fun load() = 1
    }

    class PersonService(val dao: PersonDao) {
        fun load() = dao.load()
    }

    val personService: PersonService by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            module {
                single { PersonDao() }
                single { PersonService(get()) }
            }
        )
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @Test
    fun mockkTest() {
        declareMock<PersonDao> {
            every { load() } returns 42
        }

        personService.load() shouldBe 42
    }
}