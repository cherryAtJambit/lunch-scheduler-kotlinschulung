package de.e2.koin

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.fp.Tuple2
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock


abstract class StringSpecKoinTest(vararg modules: Module, specs: StringSpecKoinTest.() -> Unit = {}) : KoinTest,
    StringSpec() {
    final override fun beforeTest(f: suspend (TestCase) -> Unit) {
        super.beforeTest(f)
    }

    final override fun afterTest(f: suspend (Tuple2<TestCase, TestResult>) -> Unit) {
        super.afterTest(f)
    }

    init {
        MockProvider.register { clazz -> mockkClass(clazz) }

        beforeTest {
            startKoin {
                modules(*modules)
            }
        }

        afterTest {
            stopKoin()
        }

        specs()
    }
}

class MockInjectTest : StringSpecKoinTest(personModule, specs = {
    "mock and inject" {
        val personService: PersonService by inject()

        declareMock<PersonDao> {
            every { load() } returns 42
        }

        personService.load() shouldBe 42
    }
})