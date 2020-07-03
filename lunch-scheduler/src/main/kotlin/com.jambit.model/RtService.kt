package de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model

import kotlinx.coroutines.channels.BroadcastChannel
import java.util.*

class RtService(val broadcastChannel : BroadcastChannel<String>) {

    private val users = mutableMapOf<String, User>()
    private val friendships = mutableMapOf<String, Friendship>()

    // creates or updates a user; returns old user if any
    suspend fun saveUser(user: User): User? {
        broadcastChannel.send("Create new user $user")
        val id = user.id ?: UUID.randomUUID().toString();
        val newUser = if (user.id == null) user.copy(id = id) else user
        return users.put(id, newUser)
    }

    // creates or updates a friendship; returns old friendship if any
    suspend fun saveFriendship(friendship: Friendship): Friendship? {
        broadcastChannel.send("Create new friendship $friendship")
        val id = friendship.id ?: UUID.randomUUID().toString();
        val newUser = if (friendship.id == null) friendship.copy(id = id) else friendship
        return friendships.put(id, newUser)
    }

    suspend fun getFriendshipById(id: String): Friendship? {
        broadcastChannel.send("Return friednship with id: $id")
        return friendships[id]
    }

    suspend fun getUserById(id: String): User? {
        broadcastChannel.send("Return user with id: $id")
        return users[id]
    }

    suspend fun findFirstUserByName(name: String): User? {
        broadcastChannel.send("Return first user with name $name")
        return users.values.firstOrNull { it.name == name }
    }

    suspend fun findEnemyUsersByUserId(userId: String): Set<User> {
        broadcastChannel.send("Return enemies by user with id: $userId")
        val allUserIds = users.values.asSequence().mapNotNull { it.id }.toSet()
        val friendIds = friendships.values.asSequence().filter { it.userId == userId }.map { it.friendUserId }.toSet()
        val enemyIds = allUserIds - friendIds;
        return enemyIds.asSequence().mapNotNull { users[it] }.toSet()
    }

    suspend fun findAllFriendUsersByUserId(userId: String): Set<User> {
        broadcastChannel.send("Return all friends for user with id: $userId")
        val friendIds = friendships.values.asSequence().filter { it.userId == userId }.map { it.friendUserId }.toSet()
        return friendIds.asSequence().mapNotNull { users[it] }.toSet()
    }
}
