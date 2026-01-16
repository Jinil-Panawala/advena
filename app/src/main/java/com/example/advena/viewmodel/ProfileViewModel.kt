package com.example.advena.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.advena.domain.Event
import com.example.advena.domain.Model
import com.example.advena.domain.User
import kotlinx.coroutines.flow.first
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

/**
 * ViewModel for the Profile screen.
 * Manages UI state and user interactions for viewing and editing user profiles.
 */
class ProfileViewModel(
    model: Model,
    private val userId: String? = null,
) : BaseViewModel(model) {

    // UI State for the Profile Screen
    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadUserData()
    }

    // ===== UI Actions =====

    private fun loadUserData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            try {
                val targetUserId = userId ?: loggedInUserId

                // Don't attempt to load user if userId is empty
                if (targetUserId.isEmpty()) {
                    uiState = uiState.copy(
                        user = null,
                        isOwnProfile = false,
                        isLoading = false,
                        hostedEvents = emptyList(),
                        attendedEvents = emptyList()
                    )
                    return@launch
                }

                val loadedUser = model.getUser(targetUserId)
                val isOwn = userId == loggedInUserId || userId == null

                // Check if logged-in user is following this profile
                var following = false
                if (!isOwn && userId != null) {
                    val followingList = model.getUserFollowing(loggedInUserId).first()
                    following = followingList.any { it.id == userId }
                }

                // Load events
                val hosted = model.getEventsHostedByUser(targetUserId).first()
                val attended = model.getEventsAttendedByUser(targetUserId).first()

                uiState = uiState.copy(
                    user = loadedUser,
                    isOwnProfile = isOwn,
                    isFollowing = following,
                    hostedEvents = hosted,
                    attendedEvents = attended,
                    isLoading = false
                )
            } catch (_: Exception) {
                // Handle user not found
                uiState = uiState.copy(
                    user = null,
                    isOwnProfile = false,
                    isLoading = false,
                    hostedEvents = emptyList(),
                    attendedEvents = emptyList()
                )
            }
        }
    }

    /**
     * Reload events for the current user
     */
    fun loadEvents() {
        viewModelScope.launch {
            try {
                val targetUserId = userId ?: loggedInUserId

                if (targetUserId.isEmpty()) return@launch

                // Load events
                val hosted = model.getEventsHostedByUser(targetUserId).first()
                val attended = model.getEventsAttendedByUser(targetUserId).first()

                uiState = uiState.copy(
                    hostedEvents = hosted,
                    attendedEvents = attended,
                )
            } catch (_: Exception) {
                // Keep current state on error
            }
        }
    }

    fun refreshUserBioName() {
        viewModelScope.launch {
            try {
                val targetUserId = userId ?: loggedInUserId

                if (targetUserId.isEmpty()) return@launch

                // Only fetch and update the user data, don't reset everything
                val updatedUser = model.getUser(targetUserId)
                uiState = uiState.copy(user = updatedUser)
                // Don't re-check isOwnProfile or isFollowing - they haven't changed
            } catch (_: Exception) {
                // Keep existing user data on error
            }
        }
    }

    // ===== Event Actions =====

    fun handleRSVP(event: Event) {
        handleRSVPWithCallback(
            event = event,
            onStart = {
                uiState = uiState.copy(updatingEvents = true)
            },
            onComplete = {
                loadEvents()
                uiState = uiState.copy(updatingEvents = false)
            },
            onError = { _ ->
                uiState = uiState.copy(updatingEvents = false)
            }
        )
    }

    fun deleteEvent(event: Event) {
        deleteEventWithCallback(
            event = event,
            onStart = {
                uiState = uiState.copy(isLoading = true)
            },
            onComplete = {
                loadEvents()
                uiState = uiState.copy(isLoading = false)
            },
            onError = { _ ->
                uiState = uiState.copy(isLoading = false)
            }
        )
    }

    // ===== Follow Actions =====

    fun toggleFollow() {
        viewModelScope.launch {
            uiState.user?.let { currentUser ->
                // Optimistically update UI immediately
                val wasFollowing = uiState.isFollowing
                uiState = uiState.copy(isFollowing = !uiState.isFollowing)

                try {
                    if (wasFollowing) {
                        model.unfollowUser(loggedInUserId, currentUser.id)
                    } else {
                        model.followUser(loggedInUserId, currentUser.id)
                    }

                    loadUserData()
                } catch (_: Exception) {
                    // Revert the state on error
                    uiState = uiState.copy(isFollowing = wasFollowing)
                }
            }
        }
    }
}

/**
 * UI state for the Profile Screen
 */
data class ProfileUiState(
    val user: User? = null,
    val isOwnProfile: Boolean = false,
    val isFollowing: Boolean = false,
    val isLoading: Boolean = false,
    val updatingEvents: Boolean = false,
    val hostedEvents: List<Event> = emptyList(),
    val attendedEvents: List<Event> = emptyList()
)

