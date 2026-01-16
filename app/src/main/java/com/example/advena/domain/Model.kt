package com.example.advena.domain

import android.util.Log
import com.example.advena.data.IStorage
import kotlinx.coroutines.flow.*

class Model(val storage: IStorage) {

    // -----------------------
    // Reactive streams
    // -----------------------
    val allUsers: Flow<List<User>> = storage.getAllUsers()

    val allEvents: Flow<List<Event>> = storage.getAllEvents()

    val loggedInUserId = MutableStateFlow<String>("")

    // -----------------------
    // User functions
    // -----------------------
    suspend fun getUser(uid: String): User = storage.getUser(uid)
    suspend fun getUserByEmail(email: String): User = storage.getUserByEmail(email)
    fun getUserFollowers(uid: String): Flow<List<User>> = storage.getUserFollowers(uid)
    fun getUserFollowing(uid: String): Flow<List<User>> = storage.getUserFollowing(uid)
    fun getEventsHostedByUser(uid: String): Flow<List<Event>> = storage.getEventsHostedByUser(uid)
    fun getEventsAttendedByUser(uid: String): Flow<List<Event>> = storage.getEventsAttendedByUser(uid)

    suspend fun createUser(id: String, name: String, email: String, bio: String = "") {
        val newUser = User(
            id = id,
            name = name,
            email = email,
            bio = bio
        )
        storage.insertUser(newUser)
    }
    suspend fun updateUser(user: User) = storage.updateUser(user)
    suspend fun deleteUser(user: User) = storage.deleteUser(user)
    suspend fun followUser(followerId: String, followeeId: String) = storage.followUser(followerId, followeeId)
    suspend fun unfollowUser(followerId: String, followeeId: String) = storage.unfollowUser(followerId, followeeId)

    // Event functions
    fun getFilteredEvents(uid:String, filters: List<EventFilter>): Flow<List<Event>> = storage.getFilteredEvents(uid,filters)
    suspend fun getEvent(eid: String): Event = storage.getEvent(eid)
    fun getEventAttendees(eid: String): Flow<List<User>> = storage.getEventAttendees(eid)
    fun getEventsByTag(tag: String): Flow<List<Event>> = storage.getEventsByTag(tag)

    suspend fun createEvent(
        id: String,
        name: String,
        description: String,
        hostId: String,
        address: String,
        latitude: Double,
        longitude: Double,
        date: String,
        startTime: String,
        endTime: String,
        tags: String = "",
        estimatedCost: Double? = null,
        maxAttendees: Int? = null,
        type: EventType,
    ) {
        val event = Event(
            id = id,
            name = name,
            description = description,
            hostId = hostId,
            address = address,
            latitude = latitude,
            longitude = longitude,
            date = date,
            startTime = startTime,
            endTime = endTime,
            tags = tags,
            estimatedCost = estimatedCost ?: 0.0,
            maxAttendees = maxAttendees ?: 0,
            type = type
        )
        storage.insertEvent(event)
    }
    suspend fun updateEvent(event: Event) = storage.updateEvent(event)
    suspend fun deleteEvent(event: Event) = storage.deleteEvent(event)
    suspend fun attendEvent(uid: String, eid: String) = storage.attendEvent(uid, eid)
    suspend fun leaveEvent(uid: String, eid: String) = storage.leaveEvent(uid, eid)
    suspend fun getAttendeesCount(eid: String): Int = storage.getAttendeesCount(eid)
    suspend fun isUserAttending(uid: String, eid: String): Boolean = storage.isUserAttending(uid, eid)
}
