package de.e2.spring.lunchScheduler


import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.RtService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

data class User(val name: String, val id: String? = null)
data class Friendship(val userId: String, val friendUserId: String, val id: String? = null)

@SpringBootApplication
class LunchApplication

@RestController
@RequestMapping("/api")
class LunchController(val rtService: RtService) {

    @GetMapping("/me", produces = [APPLICATION_JSON_VALUE])
    fun getMe(request: HttpServletRequest): User {
        val userName = request.userPrincipal.name

        return rtService.findFirstUserByName(userName)
                ?: throw Exception("No user found with name '$userName'.")
    }

    @GetMapping("/me/friends", produces = [APPLICATION_JSON_VALUE])
    fun getFriends(request: HttpServletRequest): Set<User> {
        val userName = request.userPrincipal.name
        val user = rtService.findFirstUserByName(userName)
                ?: throw Exception("No user found with name '$userName'.")

        return rtService.findAllFriendUsersByUserId(
                user.id ?: throw Exception("No user id found for user '$userName'")
        )
    }
}

fun main(args: Array<String>) {
    runApplication<LunchApplication>(*args)
}
