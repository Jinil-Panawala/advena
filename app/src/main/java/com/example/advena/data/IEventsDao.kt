package com.example.advena.data

import com.example.advena.domain.Event
import com.example.advena.domain.EventFilter
import com.example.advena.domain.User
import kotlinx.coroutines.flow.Flow

interface IEventsDao {
    fun getAllEvents(): Flow<List<Event>>
    fun getFilteredEvents(uid: String, filters: List<EventFilter>): Flow<List<Event>>
    suspend fun getEvent(eid: String): Event
    suspend fun insertEvent(event: Event)
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(event: Event)
    fun getEventsByTag(tag: String): Flow<List<Event>>
    suspend fun attendEvent(uid: String, eid: String)
    suspend fun leaveEvent(uid: String, eid: String)
    fun getEventAttendees(eid: String): Flow<List<User>>
    suspend fun getAttendeesCount(eid: String): Int
    suspend fun isUserAttending(uid: String, eid: String): Boolean
}
