package de.e2.exposed.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestContext
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object PersonTable : IntIdTable(name = "Person") {
    val vorname: Column<String> = varchar("vorname", 50)
    val nachname: Column<String> = varchar("nachname", 50)
}

object AdresseTable : IntIdTable(name = "Adresse") {
    val strasse: Column<String> = varchar("strasse", 50)
    val nummer: Column<String> = varchar("nummer", 10)
    val plz: Column<String> = varchar("plz", 5)
    val person = reference("person", PersonTable.id, onDelete = ReferenceOption.CASCADE)
}

fun StringSpec.dbTest(testName: String, test: TestContext.() -> Unit) = testName {
    Database.connect(MEM_DB, driver = "org.h2.Driver")
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(
            PersonTable,
            AdresseTable
        )
        for (p in 1..10) {
            val personId = PersonTable.insertAndGetId {
                it[vorname] = "Hans$p"
                it[nachname] = "Mustermann$p"
            }

            //performance tuning
            AdresseTable.batchInsert(1..3) { a ->
                this[AdresseTable.strasse] = "Strasse$a"
                this[AdresseTable.nummer] = "$a"
                this[AdresseTable.plz] = "1111$a"
                this[AdresseTable.person] = personId
            }
        }
        test()
    }
}

class ExposedDSLMore : StringSpec({
    dbTest("query by primary key") {
        val query = PersonTable.select { PersonTable.id eq 1 }
        val row = query.singleOrNull()
        row.shouldNotBeNull()
        row[PersonTable.nachname] shouldBe "Mustermann1"
    }

    dbTest("query and slicing") {
        val query = PersonTable
            .slice(PersonTable.nachname)
            .select { (PersonTable.nachname greater "Mustermann2") and (PersonTable.vorname lessEq "Hans4") }
        val rows = query.toList()

        rows.size shouldBe 2
        rows[0][PersonTable.nachname] shouldBe "Mustermann3"
        rows[0].hasValue(PersonTable.vorname) shouldBe false
    }

    dbTest("joining") {
        val query = (PersonTable innerJoin AdresseTable)
            .slice(
                PersonTable.nachname,
                AdresseTable.strasse
            )
            .select { AdresseTable.plz eq "11112" }

        val rows = query.toList()

        rows.size shouldBe 10
    }

    dbTest("joining with alias") {
        val p1 = PersonTable.alias("p1")
        val p2 = PersonTable.alias("p2")
        val query = (p1 crossJoin p2)
            .slice(p1[PersonTable.id], p2[PersonTable.id], p1[PersonTable.nachname])
            .select { (p1[PersonTable.nachname] eq p2[PersonTable.nachname]) and (p1[PersonTable.id] neq p2[PersonTable.id]) }

        query.count() shouldBe 0
    }

    dbTest("update") {
        PersonTable.update({ PersonTable.id eq 1 }) {
            it[PersonTable.nachname] = "Meier"
        }
        val query = PersonTable.select { PersonTable.id eq 1 }
        val row = query.single()
        row[PersonTable.nachname] shouldBe "Meier"
    }

    dbTest("delete") {
        PersonTable.deleteWhere {
            PersonTable.id eq 1
        }
        val query = PersonTable.select { PersonTable.id eq 1 }
        query.count() shouldBe 0
    }
})