package com.example.advena.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.example.advena.domain.Model
import com.example.advena.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FindFriendsViewModel(
    model: Model,
    val listType: String = "",
    id: String = "",
) : BaseViewModel(model) {

    var searchQuery by mutableStateOf("")
    var currentId by mutableStateOf(id.ifEmpty { loggedInUserId })

    // StateFlow that holds the current following list
    private val _followingList = MutableStateFlow<List<User>>(emptyList())
    val followingList: StateFlow<List<User>> = _followingList.asStateFlow()

    init {
        // Load following list initially
        refreshFollowing()
    }

    // To update the following/followers list and have UI updates be in sync
    private fun refreshFollowing() {
        viewModelScope.launch {
            try {
                model.getUserFollowing(loggedInUserId).collect { users ->
                    _followingList.value = users
                }
            } catch (_: Exception) {
                _followingList.value = emptyList()
            }
        }
    }
    private val baseUsers: Flow<List<User>> = flow {
        try {
            val sourceFlow = when (listType.lowercase()) {
                "followers" -> model.getUserFollowers(currentId)
                "following" -> model.getUserFollowing(currentId)
                "event" -> model.getEventAttendees(currentId)
                else -> model.allUsers
            }
            sourceFlow.collect { users ->
                emit(users)
            }
        } catch (_: Exception) {
            emit(emptyList())
        }
    }

    val filteredUsers: Flow<List<User>> = combine(
        baseUsers,
        snapshotFlow { searchQuery }
    ) { users, query ->
        applyUserSearchFilter(users, query)
    }

    fun followUser(uid: String) {
        viewModelScope.launch {
            try {
                model.followUser(loggedInUserId, uid)
                refreshFollowing()
            } catch (_: Exception) {
            }
        }
    }

    fun unfollowUser(uid: String) {
        viewModelScope.launch {
            try {
                model.unfollowUser(loggedInUserId, uid)
                refreshFollowing()
            } catch (_: Exception) {
            }
        }
    }
}