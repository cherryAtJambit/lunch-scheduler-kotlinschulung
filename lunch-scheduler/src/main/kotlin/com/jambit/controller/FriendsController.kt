package de.e2.lunch_scheduler.com.jambit.controller

import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.Friendship
import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.RtFriendshipRepository
import de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model.RtUserRepository
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
                val newNewFriendshipRequest: NewFriendshipRequest = call.receive()
                RtFriendshipRepository.save(newNewFriendshipRequest.newFriendship)
                call.respond(NewFriendshipResponse("Friendship established. Congratulations."))
            }

            get("/api/friends/suggest") {
                val userName = call.principal<UserIdPrincipal>()?.name ?: throw IllegalArgumentException("No username specified.")
                val user: User = RtUserRepository.getByName(userName)
                val suggestedFriends: List<User> = RtUserRepository.getAllNonFriendsById(user.id)
                call.respond(SuggestedFriendsResponse(suggestedFriends))
            }
        }
    }
}

data class SuggestedFriendsResponse(val suggestedFriends: List<User>)

data class NewFriendshipRequest(val newFriendship: Friendship)

data class NewFriendshipResponse(val result: String)
