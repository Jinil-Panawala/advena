package com.example.advena.data

import com.example.advena.domain.Event
import com.example.advena.domain.EventAttendee
import com.example.advena.domain.EventFilter
import com.example.advena.domain.EventType
import com.example.advena.domain.User
import com.example.advena.domain.UserFollower
import com.example.advena.utilities.MathUtils
import kotlinx.coroutines.flow.*

class MockStorage : IStorage {

    // In-memory storage
    private val users = mutableMapOf<String, User>()
    private val events = mutableMapOf<String, Event>()
    private val followers = mutableListOf<UserFollower>()
    private val eventAttendees = mutableListOf<EventAttendee>()

    // Flows for reactive updates
    private val usersFlow = MutableStateFlow(users.values.toList())
    private val eventsFlow = MutableStateFlow(events.values.toList())
    private val followersFlow = MutableStateFlow(followers.toList())
    private val eventAttendeesFlow = MutableStateFlow(eventAttendees.toList())

    // Users
    override fun getAllUsers(): Flow<List<User>> = usersFlow
    override suspend fun getUser(uid: String): User = users[uid]!!
    override suspend fun getUserByEmail(email: String): User = users.values.find { it.email == email }!!
    override suspend fun insertUser(user: User) {
        users[user.id] = user
        usersFlow.value = users.values.toList()
    }
    override suspend fun updateUser(user: User) {
        users[user.id] = user
        usersFlow.value = users.values.toList()
    }
    override suspend fun deleteUser(user: User) {
        val uid = user.id
        users.remove(uid)
        // Remove events hosted by this user using deleteEvent
        val hostedEvents = events.values.filter { it.hostId == uid }
        hostedEvents.forEach { event ->
            deleteEvent(event)
        }
        // Remove follow relationships
        followers.removeAll { it.followerId == uid || it.followeeId == uid }
        // Remove event attendance
        eventAttendees.removeAll { it.userId == uid }
        // Update flows
        usersFlow.value = users.values.toList()
        followersFlow.value = followers.toList()
        eventAttendeesFlow.value = eventAttendees.toList()
    }
    override suspend fun followUser(followerId: String, followeeId: String) {
        followers.add(UserFollower(followerId, followeeId))
        followersFlow.value = followers.toList()
    }
    override suspend fun unfollowUser(followerId: String, followeeId: String) {
        followers.removeIf { it.followerId == followerId && it.followeeId == followeeId }
        followersFlow.value = followers.toList()
    }
    override fun getUserFollowers(uid: String): Flow<List<User>> =
        combine(usersFlow, followersFlow) { allUsers, allFollowers ->
            val followerIds = allFollowers.filter { it.followeeId == uid }.map { it.followerId }
            allUsers.filter { it.id in followerIds }
        }
    override fun getUserFollowing(uid: String): Flow<List<User>> =
        combine(usersFlow, followersFlow) { allUsers, allFollowers ->
            val followingIds = allFollowers.filter { it.followerId == uid }.map { it.followeeId }
            allUsers.filter { it.id in followingIds }
        }

    // Events
    override fun getAllEvents(): Flow<List<Event>> = eventsFlow
    override suspend fun getEvent(eid: String): Event = events[eid]!!
    override suspend fun insertEvent(event: Event) {
        events[event.id] = event
        eventsFlow.value = events.values.toList()
    }
    override suspend fun updateEvent(event: Event) {
        events[event.id] = event
        eventsFlow.value = events.values.toList()
    }
    override suspend fun deleteEvent(event: Event) {
        events.remove(event.id)
        eventAttendees.removeAll { it.eventId == event.id }
        eventsFlow.value = events.values.toList()
        eventAttendeesFlow.value = eventAttendees.toList()
    }
    override suspend fun attendEvent(uid: String, eid: String) {
        if (!isUserAttending(uid, eid)) {
            eventAttendees.add(EventAttendee(eid, uid))
            eventAttendeesFlow.value = eventAttendees.toList()
        }
    }
    override suspend fun leaveEvent(uid: String, eid: String) {
        eventAttendees.removeIf { it.eventId == eid && it.userId == uid }
        eventAttendeesFlow.value = eventAttendees.toList()
    }
    override fun getEventAttendees(eid: String): Flow<List<User>> =
        combine(usersFlow, eventAttendeesFlow) { allUsers, attendees ->
            val attendeeIds = attendees.filter { it.eventId == eid }.map { it.userId }
            allUsers.filter { it.id in attendeeIds }
        }
    override suspend fun getAttendeesCount(eid: String): Int {
        return eventAttendees.count { it.eventId == eid }
    }
    override suspend fun isUserAttending(uid: String, eid: String): Boolean {
        return eventAttendees.any { it.userId == uid && it.eventId == eid }
    }
    override fun getEventsHostedByUser(uid: String): Flow<List<Event>> =
        eventsFlow.map { it.filter { e -> e.hostId == uid } }
    override fun getEventsAttendedByUser(uid: String): Flow<List<Event>> =
        combine(eventsFlow, eventAttendeesFlow) { allEvents, attendees ->
            val attendedIds = attendees.filter { it.userId == uid }.map { it.eventId }
            allEvents.filter { it.id in attendedIds }
        }
    override fun getEventsByTag(tag: String): Flow<List<Event>> =
        eventsFlow.map { eventsList ->
            eventsList.filter { event ->
                event.tags.split(",")
                    .map { it.trim().lowercase() }
                    .contains(tag.lowercase())
            }
        }

    private fun followerOfUserExists(followerId: String, followeeId: String): Boolean {
        return followers.any { it.followerId == followerId && it.followeeId == followeeId }
    }

    // Filters
    override fun getFilteredEvents(uid: String, filters: List<EventFilter>): Flow<List<Event>> =
        eventsFlow.map { eventsList ->
            // Separate user-related filters from other filters
            val hostedByFilters = filters.filterIsInstance<EventFilter.ByHostedBy>()
            val attendingFilters = filters.filterIsInstance<EventFilter.Attending>()
            val otherFilters = filters.filter { it !is EventFilter.ByHostedBy && it !is EventFilter.Attending }

            eventsList.filter { event ->
                // For user filters, use OR logic (hosted OR attending)
                val matchesUserFilter = if (hostedByFilters.isNotEmpty() || attendingFilters.isNotEmpty()) {
                    val isHostedByUser = hostedByFilters.any { filter -> event.hostId == filter.userId }
                    val isAttendingUser = attendingFilters.any { filter ->
                        eventAttendees.any { it.eventId == event.id && it.userId == filter.userId }
                    }
                    isHostedByUser || isAttendingUser
                } else {
                    true // No user filters specified
                }

                val isVisibleToUser  = when (event.type) {
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

                // For other filters, use AND logic (all must match)
                val matchesOtherFilters = otherFilters.all { filter ->
                    when (filter) {
                        is EventFilter.ByLocation -> {
                            val distanceKm = MathUtils.haversineDistanceKm(
                                filter.latitude, filter.longitude,
                                event.latitude, event.longitude
                            )
                            distanceKm <= filter.radiusKm
                        }
                        is EventFilter.ByTag -> event.tags.split(",")
                            .map { it.trim().lowercase() }
                            .contains(filter.tag.lowercase())
                        is EventFilter.ByAddress -> event.address.contains(filter.query, ignoreCase = true)
                        is EventFilter.ByDateRange -> event.date >= filter.startDate && event.date <= filter.endDate
                        is EventFilter.ByMaxAttendees -> event.maxAttendees <= filter.maxAttendees
                        is EventFilter.ByCost -> event.estimatedCost <= filter.maxCost
                        else -> true
                    }
                }

                // Event must match user filter (OR) AND all other filters (AND)
                matchesUserFilter && matchesOtherFilters && isVisibleToUser
            }
        }
}
