package com.example.advena.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advena.domain.Event
import com.example.advena.domain.EventType
import com.example.advena.domain.Model
import com.example.advena.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Base ViewModel containing common functionality shared across multiple ViewModels.
 * Provides event-related operations, user operations, and utility functions.
 */
abstract class BaseViewModel(protected val model: Model) : ViewModel() {

    val loggedInUserId: String
        get() = model.loggedInUserId.value

    // ===== Event-Related Functions =====

    fun getEventAttendeeCountFlow(eid: String): Flow<Int> {
        return try {
            model.getEventAttendees(eid).map { it.size }
        } catch (_: Exception) {
            flowOf(0)
        }
    }

    /**
     * Gets a Flow indicating whether the logged-in user is attending a specific event
     */
    fun getIsUserAttendingFlow(eid: String): Flow<Boolean> {
        return try {
            model.getEventAttendees(eid).map {
                model.isUserAttending(loggedInUserId, eid)
            }
        } catch (_: Exception) {
            flowOf(false)
        }
    }

    /**
     * Determines if the logged-in user owns (hosted) the given event
     */
    fun isEventOwnedByLoggedInUser(event: Event): Boolean {
        return event.hostId == loggedInUserId
    }

    /**
     * Checks visibility of an event to the logged-in user
     */
    suspend fun isEventVisibleToLoggedInUser(event: Event): Boolean {
        val eventType = event.type
        val isOwner = isEventOwnedByLoggedInUser(event)

        if (isOwner) return true

        val hostFollowers = model.getUserFollowers(event.hostId).first()
        val hostFollowing = model.getUserFollowing(event.hostId).first()

        return when (eventType) {
            EventType.PUBLIC -> true

            EventType.FOLLOWER -> {
                hostFollowers.any { follower ->
                    follower.id == loggedInUserId
                }
            }

            EventType.FRIEND -> {
                val isFollower = hostFollowers.any { it.id == loggedInUserId }
                val isFollowingBack = hostFollowing.any { it.id == loggedInUserId }

                isFollower && isFollowingBack
            }
        }
    }


    /**
     * Handles RSVP/Leave for an event with callbacks for loading states and completion
     *
     * @param event The event to RSVP to or leave
     * @param onStart Called before the operation starts (for setting loading state)
     * @param onComplete Called after successful operation (for refreshing data)
     * @param onError Called if operation fails with error message
     */
    protected fun handleRSVPWithCallback(
        event: Event,
        onStart: suspend () -> Unit = {},
        onComplete: suspend () -> Unit = {},
        onError: suspend (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                onStart()
                val attending = model.isUserAttending(loggedInUserId, event.id)
                if (attending) {
                    model.leaveEvent(loggedInUserId, event.id)
                } else {
                    model.attendEvent(loggedInUserId, event.id)
                }
                onComplete()
            } catch (e: Exception) {
                onError(e.message ?: "RSVP operation failed")
            }
        }
    }

    /**
     * Deletes an event with callbacks for loading states and completion
     *
     * @param event The event to delete
     * @param onStart Called before the operation starts (for setting loading state)
     * @param onComplete Called after successful deletion (for refreshing data)
     * @param onError Called if operation fails with error message
     */
    protected fun deleteEventWithCallback(
        event: Event,
        onStart: suspend () -> Unit = {},
        onComplete: suspend () -> Unit = {},
        onError: suspend (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                onStart()
                model.deleteEvent(event)
                onComplete()
            } catch (e: Exception) {
                onError(e.message ?: "Delete operation failed")
            }
        }
    }

    // ===== User-Related Functions =====

    /**
     * Gets a Flow of followers for a specific user
     */
    fun getFollowers(userId: String): Flow<List<User>> {
        return try {
            model.getUserFollowers(userId)
        } catch (_: Exception) {
            flowOf(emptyList())
        }
    }

    /**
     * Gets a Flow of users that a specific user is following
     */
    fun getFollowing(userId: String): Flow<List<User>> {
        return try {
            model.getUserFollowing(userId)
        } catch (_: Exception) {
            flowOf(emptyList())
        }
    }

    // ===== Search and Filter Utilities =====

    /**
     * Filters events based on a search query (name, description, or tags)
     */
    protected fun applySearchFilter(events: List<Event>, searchQuery: String): List<Event> {
        if (searchQuery.isBlank()) return events

        val query = searchQuery.lowercase().trim()
        return events.filter { event ->
            val matchesName = event.name.lowercase().contains(query)
            val matchesDescription = event.description.lowercase().contains(query)
            val matchesTag = event.tags.lowercase()
                .split(",")
                .map { it.trim() }
                .any { it.contains(query) }

            matchesName || matchesDescription || matchesTag
        }
    }

    /**
     * Filters users based on a search query (id or name)
     */
    protected fun applyUserSearchFilter(users: List<User>, searchQuery: String): List<User> {
        if (searchQuery.isBlank()) return users

        val query = searchQuery.lowercase().trim()
        return users.filter { user ->
            user.id.contains(query, ignoreCase = true) ||
                    user.name.contains(query, ignoreCase = true)
        }
    }
}