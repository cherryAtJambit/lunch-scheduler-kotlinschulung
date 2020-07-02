package de.e2.lunch_scheduler.com.jambit.controller

import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.RtService
import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.User
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing


fun Application.meController() {

    routing {
        authenticate(AUTH_NAME) {
            get("/api/me") {
                val userName = call.principal<UserIdPrincipal>()?.name ?: throw Exception("No username specified.")
                val user = RtService.findFirstUserByName(userName)
                        ?: throw Exception("No user found with name '$userName'.")

                call.respond(MeResponse(user))
            }

            get("/api/me/friends") {
                val userName = call.principal<UserIdPrincipal>()?.name ?: throw Exception("No username specified.")
                val user = RtService.findFirstUserByName(userName)
                        ?: throw Exception("No user found with name '$userName'.")

                val friends: List<User> = RtService.findFriendsByUserId(user.id)
                call.respond(FriendsResponse(friends))
            }
        }
    }
}

data class FriendsResponse(val friends: List<User>)

data class MeResponse(val me: User)
