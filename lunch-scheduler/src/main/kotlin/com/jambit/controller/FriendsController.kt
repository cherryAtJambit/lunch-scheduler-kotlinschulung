package de.e2.lunch_scheduler.com.jambit.controller

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

const val AUTH_NAME = "basic" //TOPIC const

fun Application.friendsController() {

    routing {
        authenticate(AUTH_NAME) {

            get("/api/friends") {
                call.respondText("Friend controller is running")
            }

            post("/api/friends") {
                val newNewFriendshipRequest: NewFriendshipRequest = call.receive()
                friendsRepository.add(newNewFriendshipRequest.newFriendship)
                call.respond(NewFriendshipResponse("Friendship established."))
            }

            get("/api/suggest") {
                val user: User = meRepository.getMe()
                val suggestedFriends: List<User> = friendsRepository.getAllUnFriends(user.userId)
                call.respond(SuggestedFriendsResponse(suggestedFriends))
            }
        }
    }
}

data class SuggestedFriendsResponse(val suggestedFriends: List<User>)

data class NewFriendshipRequest(val newFriendship: Friendship)

data class NewFriendshipResponse(val result: String)
