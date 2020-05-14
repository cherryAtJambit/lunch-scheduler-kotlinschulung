package de.e2.koin

import org.koin.dsl.module

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