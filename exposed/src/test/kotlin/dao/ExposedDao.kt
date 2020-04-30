package de.e2.exposed.dao

import de.e2.exposed.dsl.MEM_DB
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestContext
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object PersonTable : IntIdTable(name = "Person") {
    val vorname: Column<String> = varchar("vorname", 50)
    val nachname: Column<String> = varchar("nachname", 50)
}

class Person(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Person>(PersonTable)

    var vorname by PersonTable.vorname
    var nachname by PersonTable.nachname
    val adressen by Adresse.referrersOn(AdresseTable.person, cache = true) //cache for eager loading
}

object AdresseTable : IntIdTable(name = "Adresse") {
    val strasse: Column<String> = varchar("strasse", 50)
    val nummer: Column<String> = varchar("nummer", 10)
    val plz: Column<String> = varchar("plz", 5)
    val person = reference("person", PersonTable.id, onDelete = ReferenceOption.CASCADE)
}

class Adresse(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Adresse>(AdresseTable)

    var strasse by AdresseTable.strasse
    var nummer by AdresseTable.nummer
    var plz by AdresseTable.plz
    var person by Person referencedOn AdresseTable.person
}

fun TestContext.withDB(test: TestContext.() -> Unit) {
    Database.connect(MEM_DB, driver = "org.h2.Driver")
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(
            PersonTable,
            AdresseTable
        )
        for (p in 1..10) {
            val newPerson = Person.new {
                vorname = "Hans$p"
                nachname = "Mustermann$p"
            }
            for (a in 1..3) {
                Adresse.new {
                    strasse = "Strasse$a"
                    nummer = "$a"
                    plz = "1111$a"
                    person = newPerson
                }
            }
        }
        test()
    }
}

class ExposedDao : StringSpec({
    "query by primary key" {
        withDB {
            val person = Person.findById(1)
            person.shouldNotBeNull()
            person.nachname shouldBe "Mustermann1"
            person.adressen.count() shouldBe 3
        }
    }

    "query" {
        withDB {
            val personenIterator = Person.find {
                (PersonTable.nachname greater "Mustermann2") and (PersonTable.vorname lessEq "Hans4")
            }
            val personen = personenIterator.toList()

            personen.size shouldBe 2
            personen[0].nachname shouldBe "Mustermann3"
        }
    }

    "navigating" {
        withDB {
            val adresse = Adresse.findById(1)
            adresse.shouldNotBeNull()
            adresse.person.nachname shouldBe "Mustermann1"
        }
    }

    "joining" {
        withDB {
            val query = (PersonTable innerJoin AdresseTable)
                .slice(PersonTable.columns)
                .select { AdresseTable.plz eq "11112" }

            val personen = Person.wrapRows(query).toList()

            personen.size shouldBe 10
        }
    }

    "eager load" {
        withDB {
            val personen = Person.all().with(Person::adressen) //works only with cache together. Bug???

            personen.size shouldBe 10

            assertSoftly {
                personen.forEach { it.adressen.toList().size shouldBe 3 } //no load
            }
        }
    }

    "update" {
        withDB {
            val person = Person.findById(1)
            person.shouldNotBeNull()

            person.nachname = "Meier"
            person.adressen.forEach { it.plz = "22222" }

            //findById uses the cache only
            val personAfterUpdate = Person.find { PersonTable.id eq person.id }.single()
            personAfterUpdate.nachname shouldBe "Meier"
            assertSoftly {
                personAfterUpdate.adressen.forEach {
                    it.plz shouldBe "22222"
                }
            }
        }
    }

    "delete" {
        withDB {
            val person = Person.findById(1)
            person.shouldNotBeNull()

            person.delete()

            val personAfterUpdate = Person.find { PersonTable.id eq person.id }
            personAfterUpdate.count() shouldBe 0
        }
    }

    "delete adresse" {
        withDB {
            val person = Person.findById(1)
            person.shouldNotBeNull()

            person.adressen.first().delete()
            person.adressen.count() shouldBe 2

            val personAfterUpdate = Person.find { PersonTable.id eq person.id }.single()
            personAfterUpdate.adressen.count() shouldBe 2
        }
    }
})