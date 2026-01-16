package com.example.advena.data

import android.R
import android.util.Log
import com.example.advena.domain.Event
import com.example.advena.domain.EventFilter
import com.example.advena.domain.User
import kotlinx.coroutines.flow.Flow

/**
 * Database-backed implementation of IStorage using Room DAOs.
 *
 * @param eventsDao The DAO for event-related operations
 * @param usersDao The DAO for user-related operations
 */
class DbStorage(
    private val eventsDao: IEventsDao = EventsDao(),
    private val usersDao: IUsersDao = UsersDao()
) : IStorage {

    // ==================== Users ====================

    override fun getAllUsers(): Flow<List<User>> {
        return usersDao.getAllUsers()
    }

    override suspend fun getUser(uid: String): User {
        return usersDao.getUser(uid)
    }

    override suspend fun getUserByEmail(email: String): User {
        return usersDao.getUserByEmail(email)
    }

    override suspend fun insertUser(user: User) {
        usersDao.insertUser(user)
    }

    override suspend fun updateUser(user: User) {
        usersDao.updateUser(user)
    }

    override suspend fun deleteUser(user: User) {
        usersDao.deleteUser(user)
    }

    override suspend fun followUser(followerId: String, followeeId: String) {
        usersDao.followUser(followerId, followeeId)
    }

    override suspend fun unfollowUser(followerId: String, followeeId: String) {
        usersDao.unfollowUser(followerId, followeeId)
    }

    override fun getUserFollowers(uid: String): Flow<List<User>> {
        return usersDao.getUserFollowers(uid)
    }

    override fun getUserFollowing(uid: String): Flow<List<User>> {
        return usersDao.getUserFollowing(uid)
    }

    override fun getEventsHostedByUser(uid: String): Flow<List<Event>> {
        return usersDao.getEventsHostedByUser(uid)
    }

    override fun getEventsAttendedByUser(uid: String): Flow<List<Event>> {
        return usersDao.getEventsAttendedByUser(uid)
    }

    // ==================== Events ====================

    override fun getAllEvents(): Flow<List<Event>> {
        return eventsDao.getAllEvents()
    }

    override fun getFilteredEvents(uid: String, filters: List<EventFilter>): Flow<List<Event>> {
        return eventsDao.getFilteredEvents(uid, filters)
    }

    override suspend fun getEvent(eid: String): Event {
        return eventsDao.getEvent(eid)
    }

    override suspend fun insertEvent(event: Event) {
        eventsDao.insertEvent(event)
    }

    override suspend fun updateEvent(event: Event) {
        eventsDao.updateEvent(event)
    }

    override suspend fun deleteEvent(event: Event) {
        eventsDao.deleteEvent(event)
    }

    override suspend fun attendEvent(uid: String, eid: String) {
        eventsDao.attendEvent(uid, eid)
    }

    override suspend fun leaveEvent(uid: String, eid: String) {
        eventsDao.leaveEvent(uid, eid)
    }

    override fun getEventAttendees(eid: String): Flow<List<User>> {
        return eventsDao.getEventAttendees(eid)
    }

    override suspend fun getAttendeesCount(eid: String): Int {
        return eventsDao.getAttendeesCount(eid)
    }

    override suspend fun isUserAttending(uid: String, eid: String): Boolean {
        return eventsDao.isUserAttending(uid, eid)
    }

    override fun getEventsByTag(tag: String): Flow<List<Event>> {
        return eventsDao.getEventsByTag(tag)
    }
}

