package com.example.advena.data
import com.example.advena.domain.Event
import com.example.advena.domain.EventFilter
import com.example.advena.domain.User
import kotlinx.coroutines.flow.Flow

interface IStorage {

    // Users
    fun getAllUsers(): Flow<List<User>>
    suspend fun getUser(uid: String): User
    suspend fun getUserByEmail(email: String): User
    suspend fun insertUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun followUser(followerId: String, followeeId: String)
    suspend fun unfollowUser(followerId: String, followeeId: String)
    fun getUserFollowers(uid: String): Flow<List<User>>
    fun getUserFollowing(uid: String): Flow<List<User>>
    fun getEventsHostedByUser(uid: String): Flow<List<Event>>
    fun getEventsAttendedByUser(uid: String): Flow<List<Event>>

    // Events
    fun getAllEvents(): Flow<List<Event>>
    fun getFilteredEvents(uid: String, filters: List<EventFilter>): Flow<List<Event>>
    suspend fun getEvent(eid: String): Event
    suspend fun insertEvent(event: Event)
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(event: Event)
    suspend fun attendEvent(uid: String, eid: String)
    suspend fun leaveEvent(uid: String, eid: String)
    fun getEventAttendees(eid: String): Flow<List<User>>
    suspend fun getAttendeesCount(eid: String): Int
    suspend fun isUserAttending(uid: String, eid: String): Boolean
    fun getEventsByTag(tag: String): Flow<List<Event>>
}
