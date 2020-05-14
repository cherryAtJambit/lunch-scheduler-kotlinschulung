package de.e2.spring.annotations

import org.springframework.data.repository.CrudRepository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Adresse(
    var strasse: String,
    var hausnummer: String,
    @Column(length = 5) var plz: String,
    var stadt: String,
    @Id @GeneratedValue var id: Long? = null
)

interface AdresseRepository: CrudRepository<Adresse, Long> {
    fun findAllByStadt(stadt: String)
}