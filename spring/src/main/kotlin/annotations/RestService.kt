package de.e2.spring.annotations

import de.e2.spring.annotations.AdresseRepository
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
    fun findAll() = repository.findAll()

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) =
        repository.findByIdOrNull(id)?:throw ResponseStatusException(HttpStatus.NOT_FOUND, "This address does not exist")
}