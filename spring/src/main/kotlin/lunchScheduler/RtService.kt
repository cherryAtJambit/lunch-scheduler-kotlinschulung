package de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model

import de.e2.spring.lunchScheduler.Friendship
import de.e2.spring.lunchScheduler.User
import java.util.*

class RtService() {

    private val users = mutableMapOf<String, User>()
    private val friendships = mutableMapOf<String, Friendship>()

    // creates or updates a user; returns old user if any
    fun saveUser(user: User): User? {
        val id = user.id ?: UUID.randomUUID().toString();
        val newUser = if (user.id == null) user.copy(id = id) else user
        return users.put(id, newUser)
    }

    // creates or updates a friendship; returns old friendship if any
    fun saveFriendship(friendship: Friendship): Friendship? {
        val id = friendship.id ?: UUID.randomUUID().toString();
        val newUser = if (friendship.id == null) friendship.copy(id = id) else friendship
        return friendships.put(id, newUser)
    }

    fun getFriendshipById(id: String): Friendship? {
        return friendships[id]
    }

    fun getUserById(id: String): User? {
        return users[id]
    }

    fun findFirstUserByName(name: String): User? {
        return users.values.firstOrNull { it.name == name }
    }

    fun findEnemyUsersByUserId(userId: String): Set<User> {
        val allUserIds = users.values.asSequence().mapNotNull { it.id }.toSet()
        val friendIds = friendships.values.asSequence().filter { it.userId == userId }.map { it.friendUserId }.toSet()
        val enemyIds = allUserIds - friendIds;
        return enemyIds.asSequence().mapNotNull { users[it] }.toSet()
    }

    fun findAllFriendUsersByUserId(userId: String): Set<User> {
        val friendIds = friendships.values.asSequence().filter { it.userId == userId }.map { it.friendUserId }.toSet()
        return friendIds.asSequence().mapNotNull { users[it] }.toSet()
    }
}