package de.e2.koin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock

class MockingWithKoinKoTest : StringSpec({
    "mock" {
        class PersonDao {
            fun load() = 1
        }

        class PersonService(val dao: PersonDao) {
            fun load() = dao.load()
        }

        val personModule = module {
            single { PersonDao() }
            single { PersonService(get()) }
        }

        val application = koinApplication {
            modules(personModule)
        }

        application.koin.declareMock<PersonDao> {
            every { load() } returns 42
        }

        application.koin.get<PersonService>().load() shouldBe 42
    }
}) {
    init {
        MockProvider.register { clazz -> mockkClass(clazz) }
    }
}