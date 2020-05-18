package de.e2.exposed

import io.kotest.core.spec.style.StringSpec
import io.r2dbc.h2.H2ConnectionConfiguration
import io.r2dbc.h2.H2ConnectionFactory
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import reactor.core.publisher.Flux

const val INIT_SCRIPT = """
            DROP TABLE IF EXISTS customer;
            CREATE TABLE customer ( id SERIAL PRIMARY KEY, firstname VARCHAR(100) NOT NULL, lastname VARCHAR(100) NOT NULL);
            INSERT INTO customer (firstname, lastname) VALUES ('Max','Mustermann');
            INSERT INTO customer (firstname, lastname) VALUES ('Maxi','Musterfrau');
            """

class BasicR2DBC : StringSpec({
    "r2dbc" {
        val h2ConnectionFactory = H2ConnectionFactory(
            H2ConnectionConfiguration.builder()
                .url("mem:test")
                .username("sa")
                .build()
        )

        val poolOptions = ConnectionPoolConfiguration.builder(h2ConnectionFactory).build()
        val pool = ConnectionPool(poolOptions)
        val statements = pool.create().flatMap { conn ->
            Flux.from(conn.createStatement(INIT_SCRIPT).execute()).last()
                .and(
                    Flux.from(conn.createStatement("select * from customer").execute())
                        .flatMap { result ->
                            result.map { row, meta -> meta.columnNames.map { it to row[it] }.toMap() }
                        }.doOnNext {
                            println(it)
                        }
                ).doFinally { conn.close() }

        }
        statements.block()
        pool.close()
    }

    "r2dbc coroutines" {
        val h2ConnectionFactory = H2ConnectionFactory(
            H2ConnectionConfiguration.builder()
                .url("mem:test")
                .username("sa")
                .build()
        )

        val poolOptions = ConnectionPoolConfiguration.builder(h2ConnectionFactory).build()
        val pool = ConnectionPool(poolOptions)
        val conn = pool.create().awaitSingle()
        try {
            conn.createStatement(INIT_SCRIPT).execute().awaitLast()

            val result = conn.createStatement("select * from customer").execute().awaitSingle()
            val customers = result.map { row: Row, meta ->
                meta.columnNames.map { it to row[it] }.toMap()
            }.asFlow().toList()
            customers.forEach { println(it) }
        } finally {
            conn.close().awaitFirstOrNull()
            pool.close()
        }

    }
})