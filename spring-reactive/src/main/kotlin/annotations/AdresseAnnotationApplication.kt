package de.e2.springreactive.annotations

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.r2dbc.core.DatabaseClient

@SpringBootApplication
class AdresseAnnotationApplication {
    @Bean
    fun connectionFactory(): ConnectionFactory {
        val h2ConnectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///test")
        val poolOptions = ConnectionPoolConfiguration.builder(h2ConnectionFactory).build()
        val pool = ConnectionPool(poolOptions)
        return pool
    }
    @Bean
    fun databaseClient(): DatabaseClient {
        return DatabaseClient.create(connectionFactory())
    }
}

fun main(args: Array<String>) {
    runApplication<AdresseAnnotationApplication>(*args) //#TOPIC Spreading
}