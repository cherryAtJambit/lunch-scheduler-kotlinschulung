package de.e2.spring.lunchScheduler


import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.RtService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import javax.servlet.http.HttpServletRequest

data class LunchUser(val name: String, val id: String? = null)

data class Friendship(val userId: String, val friendUserId: String, val id: String? = null)

@SpringBootApplication
class LunchApplication

@RestController
@RequestMapping("/api")
class LunchController(val rtService: RtService) {

    @GetMapping("/me", produces = [APPLICATION_JSON_VALUE])
    fun getMe(request: HttpServletRequest): LunchUser {
        val userName = request.userPrincipal.name

        return rtService.findFirstUserByName(userName)
                ?: throw Exception("No user found with name '$userName'.")
    }

    @GetMapping("/me/friends", produces = [APPLICATION_JSON_VALUE])
    fun getFriends(request: HttpServletRequest): Set<LunchUser> {
        val userName = request.userPrincipal.name
        val user = rtService.findFirstUserByName(userName)
                ?: throw Exception("No user found with name '$userName'.")

        return rtService.findAllFriendUsersByUserId(
                user.id ?: throw Exception("No user id found for user '$userName'")
        )
    }
}

@EnableWebSecurity
class KotlinSecurityConfiguration : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http {
            httpBasic {}
            authorizeRequests {
                authorize("/greetings/**", hasAuthority("ROLE_ADMIN"))
                authorize("/**", permitAll)
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<LunchApplication>(*args) {
        addInitializers(beans {
            bean {

                fun user(user: String, pw: String, vararg roles: String) =
                        User.withDefaultPasswordEncoder().username(user).password(pw).roles(*roles).build()

                InMemoryUserDetailsManager(user("admin", "admin", "USER"))
            }
            bean {
                router {
                    GET("/greetings") { request ->
                        request.principal().map { it.name }.map { ServerResponse.ok().body(mapOf("greeting" to "Hello, $it")) }.orElseGet { ServerResponse.badRequest().build() }
                    }
                }
            }
        })
    }
}
