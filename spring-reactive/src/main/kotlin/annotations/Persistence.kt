package de.e2.springreactive.annotations

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.asType
import org.springframework.data.r2dbc.core.from
import org.springframework.data.r2dbc.core.into
import org.springframework.data.r2dbc.core.table
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class Adresse(
    var strasse: String,
    var hausnummer: String,
    var plz: String,
    var stadt: String,
    var id: Long? = null
)

@Repository
class AdresseRepository(val databaseClient: DatabaseClient) {
    suspend fun findByIdCriteria(id: Long): Adresse {
        return databaseClient.select().from<Adresse>()
            .matching(where("id").`is`(id)).fetch().one().awaitFirst()
    }

    suspend fun findById(id: Long): Adresse {
        return databaseClient.execute("SELECT * FROM ADRESSE WHERE id=:id")
            .bind("id", id)
            .asType<Adresse>()
            .fetch().one().awaitFirst()
    }

    fun findAll(): Flow<Adresse> {
        return databaseClient.select().from<Adresse>().fetch().all().asFlow()
    }

    suspend fun save(adresse: Adresse): Int {
        if (adresse.id != null) {
            return databaseClient.update().table<Adresse>().using(adresse).fetch().rowsUpdated().awaitFirst()
        }
        return databaseClient.insert().into<Adresse>().using(adresse).fetch().rowsUpdated().awaitFirst()
    }
}


