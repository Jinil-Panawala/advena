package com.example.advena.data

import android.util.Log
import com.example.advena.domain.Event
import com.example.advena.domain.EventAttendee
import com.example.advena.domain.EventFilter
import com.example.advena.domain.EventType
import com.example.advena.domain.User
import com.example.advena.domain.UserFollower
import com.example.advena.utilities.MathUtils
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

// Helper filter functions
fun filterByLocation(latitude: Double, longitude: Double, radiusKm: Double): (Event) -> Boolean = { event ->
    val distanceKm = MathUtils.haversineDistanceKm(
        latitude, longitude,
        event.latitude, event.longitude
    )
    distanceKm <= radiusKm
}

fun filterByTag(tag: String): (Event) -> Boolean = { event ->
    event.tags.split(",")
        .map { it.trim().lowercase() }
        .contains(tag.lowercase())
}

fun filterByDateRange(startDate: String, endDate: String): (Event) -> Boolean = { event ->
    event.date in startDate..endDate
}

fun filterByAddress(query: String): (Event) -> Boolean = { event ->
    event.address.contains(query, ignoreCase = true)
}

fun filterByMaxAttendees(maxAttendees: Int): (Event) -> Boolean = { event ->
    event.maxAttendees <= maxAttendees
}

fun filterByCost(maxCost: Double): (Event) -> Boolean = { event ->
    event.estimatedCost <= maxCost
}

/**
 * Implements IEventsDao interface by interacting with the supabase db.
 */
class EventsDao : IEventsDao {

    private val supabase = SupabaseClient.client

    override fun getAllEvents(): Flow<List<Event>> = flow {
        val events = supabase.from("Events")
            .select()
            .decodeList<Event>()
        emit(events)
    }

    override suspend fun getEvent(eid: String): Event {
        return supabase.from("Events")
            .select {
                filter {
                    eq("eid", eid)
                }
            }
            .decodeSingle<Event>()
    }

    override suspend fun insertEvent(event: Event) {
        try {
            val response = supabase.from("Events").insert(event)
        } catch (e: Exception) {
        }
    }

    override suspend fun updateEvent(event: Event) {
        supabase.from("Events").update(event) {
            filter {
                eq("eid", event.id)
            }
        }
    }

    override suspend fun deleteEvent(event: Event) {
        supabase.from("Events").delete {
            filter {
                eq("eid", event.id)
            }
        }
    }

    override fun getEventsByTag(tag: String): Flow<List<Event>> = flow {
        val events = supabase.from("Events")
            .select {
                filter {
                    ilike("tags", "%$tag%")
                }
            }
            .decodeList<Event>()
        emit(events)
    }


    // Events where users are hosts or attendees
    // Union between Events and Event-Attendees JOIN Events for each user
    private fun getEventsByHostOrAttendees(userIds: List<String>): Flow<List<Event>> = flow {
        val hostedEvents = supabase.from("Events")
            .select {
                filter {
                    isIn("host_id", userIds)
                }
            }
            .decodeList<Event>()

        val attended = supabase.from("Event-Attendees")
            .select {
                filter {
                    isIn("uid", userIds)
                }
            }
            .decodeList<EventAttendee>()

        val attendedEventIds = attended.map { it.eventId }.distinct()

        // Get events attended by the users
        val attendedEvents = if (attendedEventIds.isEmpty()) {
            emptyList()
        } else {
            supabase.from("Events")
                .select {
                    filter {
                        isIn("eid", attendedEventIds)
                    }
                }
                .decodeList<Event>()
        }

        // Combine and remove duplicates (union operation)
        val allEvents = (hostedEvents + attendedEvents).distinctBy { it.id }
        emit(allEvents)
    }

    private suspend fun insertEventAttendee(eventAttendee: EventAttendee) {
        supabase.from("Event-Attendees").upsert(eventAttendee)
    }

    private suspend fun deleteEventAttendee(uid: String, eid: String) {
        supabase.from("Event-Attendees").delete {
            filter {
                eq("uid", uid)
                eq("eid", eid)
            }
        }
    }

    private suspend fun followerOfUserExists(followerId: String, followeeId: String): Boolean {
        return try {
            supabase.from("User-Followers")
                .select {
                    filter {
                        eq("followerid", followerId)
                        eq("followeeid", followeeId)
                    }
                }
                .decodeSingleOrNull<UserFollower>() != null
        } catch (_: Exception) {
            false
        }
    }

    // Get the users attending this event
    // Join between Event-Attendees and Users
    override fun getEventAttendees(eid: String): Flow<List<User>> = flow {
        val attendanceRecords = supabase.from("Event-Attendees")
            .select {
                filter {
                    eq("eid", eid)
                }
            }
            .decodeList<EventAttendee>()

        val userIds = attendanceRecords.map { it.userId }

        // Get the users attending
        val users = if (userIds.isEmpty()) {
            emptyList()
        } else {
            supabase.from("Users")
                .select {
                    filter {
                        isIn("uid", userIds)
                    }
                }
                .decodeList<User>()
        }
        emit(users)
    }

    override suspend fun getAttendeesCount(eid: String): Int {
        val result = supabase.from("Event-Attendees")
            .select {
                filter {
                    eq("eid", eid)
                }
            }
            .decodeList<EventAttendee>()
        return result.size
    }

    override suspend fun isUserAttending(uid: String, eid: String): Boolean {
        return try {
            supabase.from("Event-Attendees")
                .select {
                    filter {
                        eq("uid", uid)
                        eq("eid", eid)
                    }
                }
                .decodeSingleOrNull<EventAttendee>() != null
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun attendEvent(uid: String, eid: String) {
        if (!isUserAttending(uid, eid)) {
            insertEventAttendee(EventAttendee(eid, uid))
        }
    }

    override suspend fun leaveEvent(uid: String, eid: String) {
        deleteEventAttendee(uid, eid)
    }

    override fun getFilteredEvents(uid: String, filters: List<EventFilter>): Flow<List<Event>> {
        // Extract user-related filters
        val hostedByFilters = filters.filterIsInstance<EventFilter.ByHostedBy>()
        val attendingFilters = filters.filterIsInstance<EventFilter.Attending>()
        val otherFilters = filters.filter { it !is EventFilter.ByHostedBy && it !is EventFilter.Attending}

        // Determine which events to start with based on user filters
        val baseFlow: Flow<List<Event>> = if (hostedByFilters.isNotEmpty() || attendingFilters.isNotEmpty()) {
            // Collect all user IDs from both filter types
            val userIds = (hostedByFilters.map { it.userId } + attendingFilters.map { it.userId }).distinct()

            getEventsByHostOrAttendees(userIds) // covers cases where we have one userId or multiple

        } else {
            getAllEvents()
        }

        // Apply remaining filters in-memory
        return baseFlow.map { eventsList ->
            eventsList.filter { event ->

                // remove events that shouldn't be visible to user based on type (friends-only, private)
                val isVisible = when (event.type) {
                    EventType.PUBLIC -> true
                    EventType.FOLLOWER -> {
                        // check if user is follower of host or is host
                        followerOfUserExists(uid, event.hostId) || event.hostId == uid
                    }
                    EventType.FRIEND -> {
                        // check if user and host are followers of each other or is host
                        event.hostId == uid ||
                                (followerOfUserExists(uid, event.hostId) &&
                                        followerOfUserExists(event.hostId, uid))
                    }
                }

                if (!isVisible) {
                    return@filter false
                }

                otherFilters.all { filter ->
                    when (filter) {
                        is EventFilter.ByLocation -> filterByLocation(
                            filter.latitude, filter.longitude, filter.radiusKm
                        )(event)
                        is EventFilter.ByTag -> filterByTag(filter.tag)(event)
                        is EventFilter.ByAddress -> filterByAddress(filter.query)(event)
                        is EventFilter.ByDateRange -> filterByDateRange(filter.startDate, filter.endDate)(event)
                        is EventFilter.ByMaxAttendees -> filterByMaxAttendees(filter.maxAttendees)(event)
                        is EventFilter.ByCost -> filterByCost(filter.maxCost)(event)
                        else -> true
                    }
                }
            }
        }
    }
}
