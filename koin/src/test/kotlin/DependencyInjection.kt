package de.e2.koin

import io.kotest.core.Tag
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockkClass
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.inject
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock
import java.util.*

interface AdresseDao


object KoinInject : Tag()

class DependencyInjection : StringSpec({
    "declare modules and use koin" {
        class PersonDao
        class PersonService(val dao: PersonDao)

        val personModule = module {
            single { PersonDao() }
            single { PersonService(get()) }

            factory { UUID.randomUUID() }
        }

        val app = koinApplication {
            modules(personModule)
        }

        val personService = app.koin.getOrNull<PersonService>()
        personService.shouldNotBeNull()

        val uuid1 = app.koin.get<UUID>()
        val uuid2 = app.koin.get<UUID>()

        uuid1 shouldNotBe uuid2
    }

    "bind with interfaces" {
        class AdresseDaoImpl() : AdresseDao
        class AdresseService(val dao: AdresseDao)

        val adresseModule = module {
            single<AdresseDao> { AdresseDaoImpl() }
            single { AdresseService(get()) }
        }

        val app = koinApplication {
            modules(adresseModule)
        }

        app.koin.getOrNull<AdresseDao>().shouldNotBeNull()
        app.koin.getOrNull<AdresseDaoImpl>().shouldBeNull()
    }

    "multiple implementations" {
        class AdresseDaoImpl1() : AdresseDao
        class AdresseDaoImpl2() : AdresseDao
        class AdresseService(val dao: AdresseDao)

        val adresseModule = module {
            single { AdresseDaoImpl1() } bind AdresseDao::class
            single { AdresseDaoImpl2() } bind AdresseDao::class
            single { AdresseService(get()) }
        }

        val app = koinApplication {
            modules(adresseModule)
        }

        app.koin.get<AdresseDao>().shouldNotBeNull()
        app.koin.get<AdresseDaoImpl1>().shouldNotBeNull()
        app.koin.getAll<AdresseDao>().shouldHaveSize(2)
    }

    "named beans" {
        class AdresseDaoImpl1() : AdresseDao
        class AdresseDaoImpl2() : AdresseDao
        class AdresseService(val dao: AdresseDao)

        val adresseModule = module {
            single<AdresseDao>(qualifier("dao1")) { AdresseDaoImpl1() }
            single<AdresseDao>(qualifier("dao2")) { AdresseDaoImpl2() }
            single { AdresseService(get(qualifier("dao1"))) }
        }

        val app = koinApplication {
            modules(adresseModule)
        }

        app.koin.get<AdresseDao>(named("dao1")).shouldNotBeNull()
        app.koin.getAll<AdresseDao>().shouldHaveSize(2)
    }

    "multiple modules" {
        class AdresseDaoImpl() : AdresseDao
        class AdresseService(val dao: AdresseDao)

        val adresseModule1 = module {
            single<AdresseDao> { AdresseDaoImpl() }
        }

        val adresseModule2 = module {
            single { AdresseService(get()) }
        }

        val app = koinApplication {
            modules(adresseModule1, adresseModule2)
        }

        app.koin.get<AdresseService>().shouldNotBeNull()
        app.koin.get<AdresseDao>().shouldNotBeNull()
    }

    "mocking" {
        class PersonDao {
            fun load() = 1
        }

        class PersonService(val dao: PersonDao) {
            fun load() = dao.load()
        }

        val app = koinApplication {
            modules(
                module {
                    single { PersonDao() }
                    single { PersonService(get()) }
                }
            )
        }

        //muss man nur einmal machen, nicht pro Test
        MockProvider.register { clazz -> mockkClass(clazz) }

        app.koin.declareMock<PersonDao> {
            every { load() } returns 42
        }

        val personService = app.koin.getOrNull<PersonService>()
        personService.shouldNotBeNull()

        personService.load() shouldBe 42
    }

    "KoinComponent inject".config(tags = setOf(KoinInject)) {
        class PersonDao
        class PersonService(val dao: PersonDao)

        val personModule = module {
            single { PersonDao() }
            single { PersonService(get()) }
        }

        startKoin {
            modules(personModule)
        }

        class PersonController() : KoinComponent {
            val personService: PersonService by inject()
        }

        val personController = PersonController()
        personController.personService.shouldNotBeNull()
    }

    afterTest { (testCase, _) -> //#TOPIC: Destructuring and _
        if (testCase.config.tags.contains(KoinInject)) {
            //Damit es nicht im Test vergessen wird
            stopKoin()
        }
    }
})