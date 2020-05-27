package de.e2.springreactive.annotations

import de.e2.springreactive.annotations.AdresseRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/adresse")
class AdresseController(val repository: AdresseRepository) {
    @GetMapping("/")
    fun findAll(): Flow<Adresse> = repository.findAll()

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable id: Long) =
        repository.findById(id)
}