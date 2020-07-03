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
import org.koin.ktor.ext.inject


fun Application.meController() {

    val rtService: RtService by inject()

    routing {
        authenticate(AUTH_NAME) {
            get("/api/me") {
                try {
                    val userName = call.principal<UserIdPrincipal>()?.name ?: throw Exception("No username specified.")
                    val user = rtService.findFirstUserByName(userName)
                            ?: throw Exception("No user found with name '$userName'.")

                    call.respond(MeResponse(user))
                } catch (ex: Exception) {
                    call.respond(ErrorResponse(ex.message ?: "An error occurred."))
                }
            }

            get("/api/me/friends") {
                try {
                    val userName = call.principal<UserIdPrincipal>()?.name ?: throw Exception("No username specified.")

                    val user = rtService.findFirstUserByName(userName)
                            ?: throw Exception("No user found with name '$userName'.")

                    val friends: Set<User> = rtService.findAllFriendUsersByUserId(
                            user.id ?: throw Exception("No user id found for user '$userName'")
                    )
                    call.respond(FriendsResponse(friends))
                } catch (ex: Exception) {
                    call.respond(ErrorResponse(ex.message ?: "An error occurred."))
                }
            }
            get("/api/me/events") {
                try {
                    val userName = call.principal<UserIdPrincipal>()?.name ?: throw Exception("No username specified.")

                    val user = rtService.findFirstUserByName(userName)
                            ?: throw Exception("No user found with name '$userName'.")

                    val friends: Set<User> = rtService.findAllFriendUsersByUserId(
                            user.id ?: throw Exception("No user id found for user '$userName'")
                    )
                    call.respond(FriendsResponse(friends))
                } catch (ex: Exception) {
                    call.respond(ErrorResponse(ex.message ?: "An error occurred."))
                }
            }
        }
    }
}

data class FriendsResponse(val friends: Set<User>)

data class MeResponse(val me: User)
