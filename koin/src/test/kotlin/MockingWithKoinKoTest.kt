package de.e2.koin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock

class PersonDao {
    fun load() = 1
}

class PersonService(val dao: PersonDao) {
    fun load() = dao.load()
}

class MockingWithKoinKoTest : KoinTest, StringSpec() {
    val personService: PersonService by inject()

    init {
        MockProvider.register { clazz -> mockkClass(clazz) }

        beforeTest {
            startKoin {
                modules(
                    module {
                        single { PersonDao() }
                        single { PersonService(get()) }
                    }
                )
            }
        }

        afterTest {
            stopKoin()
        }

        "mock" {

            declareMock<PersonDao> {
                every { load() } returns 42
            }

            personService.load() shouldBe 42
        }
    }
}