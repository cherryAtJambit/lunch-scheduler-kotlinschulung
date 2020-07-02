package de.e2.lunch_scheduler.com.jambit.controller

import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.Friendship
import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.RtService
import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.User
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

const val AUTH_NAME = "basic" //TOPIC const

fun Application.friendsController() {

    routing {
        authenticate(AUTH_NAME) {

            post("/api/friends") {
                try {
                    val newNewFriendshipRequest: NewFriendshipRequest = call.receive()
                    RtService.saveFriendship(newNewFriendshipRequest.newFriendship)
                    call.respond(NewFriendshipResponse("Friendship established. Congratulations."))
                } catch (ex: Exception) {
                    call.respond(ErrorResponse(ex.message ?: "An error occurred."))
                }
            }

            get("/api/friends/suggest") {
                try {

                    val userName = call.principal<UserIdPrincipal>()?.name ?: throw Exception("No username specified.")
                    val user: User = RtService.findFirstUserByName(userName)
                            ?: throw Exception("No user found with name '$userName'.")
                    val suggestedFriends: Set<User> = RtService.findEnemyUsersByUserId(
                            user.id ?: throw Exception("No user id found for user '$userName'")
                    )
                    call.respond(SuggestedFriendsResponse(suggestedFriends))
                } catch (ex: Exception) {
                    call.respond(ErrorResponse(ex.message ?: "An error occurred."))
                }
            }
        }
    }
}

data class SuggestedFriendsResponse(val suggestedFriends: Set<User>)

data class NewFriendshipRequest(val newFriendship: Friendship)

data class NewFriendshipResponse(val result: String)

data class ErrorResponse(val errorMessage: String)
