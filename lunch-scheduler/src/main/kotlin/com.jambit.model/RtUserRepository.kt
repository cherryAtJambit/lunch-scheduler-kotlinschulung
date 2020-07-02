package de.e2.lunch_scheduler.de.e2.lunch_scheduler.com.jambit.model

import java.util.*

object RtUserRepository {

    private val users = mutableMapOf<String, User>()

    fun save(user: User): User? {
        val id:String = user.id ?: UUID.randomUUID().toString();
        val newUser: User = if (user.id == null) User(id, user.name) else user
        return users.put(id, newUser)
    }

    fun getById(id:String): User? {
        return users[id]
    }
}