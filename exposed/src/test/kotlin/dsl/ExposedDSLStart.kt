package de.e2.exposed.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.DriverManager

object Person : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val vorname: Column<String> = varchar("vorname", 50)
    val nachname: Column<String> = varchar("nachname", 50)
    override val primaryKey = PrimaryKey(id, name = "PK_Person_Id")
}

const val MEM_DB = "jdbc:h2:mem:test"

class ExposedDSLStart : StringSpec({
    "create tables" {
        Database.connect(MEM_DB, driver = "org.h2.Driver")

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Person)

            DriverManager.getConnection(MEM_DB).use {
                val tables = it.metaData.getTables(null, null, "PERSON", null)
                tables.next() shouldBe true
            }
        }
    }

    "insert row" {
        Database.connect(MEM_DB, driver = "org.h2.Driver")

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Person)

            val row = Person.insert {
                it[vorname] = "Hans"
                it[nachname] = "Mustermann"
            }

            val newId = row[Person.id]
            newId shouldBe 1
        }
    }
})