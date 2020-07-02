package de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model

import java.util.*

object RtFriendshipRepository {

    private val friendships = mutableMapOf<String, Friendship>()

    fun save(friendship: Friendship): Friendship? {
        val id: String = friendship.id ?: UUID.randomUUID().toString();
        val newUser: Friendship = if (friendship.id == null) Friendship(id, friendship.userId, friendship.friendUserId) else friendship
        return friendships.put(id, newUser)
    }

    fun getById(id: String): Friendship? {
        return friendships[id]
    }
}