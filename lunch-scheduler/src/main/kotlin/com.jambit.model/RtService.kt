package de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model

import java.util.*

object RtService {

    private val users = mutableMapOf<String, User>()
    private val friendships = mutableMapOf<String, Friendship>()

    fun saveUser(user: User): User? {
        val id: String = user.id ?: UUID.randomUUID().toString();
        val newUser: User = if (user.id == null) User(id, user.name) else user
        return users.put(id, newUser)
    }

    fun saveFriendship(friendship: Friendship): Friendship? {
        val id: String = friendship.id ?: UUID.randomUUID().toString();
        val newUser: Friendship = if (friendship.id == null) Friendship(id, friendship.userId, friendship.friendUserId) else friendship
        return friendships.put(id, newUser)
    }

    fun getFriendshipById(id: String): Friendship? {
        return friendships[id]
    }

    fun getUserById(id: String): User? = users[id]

    fun findFirstUserByName(name: String): User? = users.values.firstOrNull { it.name == name }
}