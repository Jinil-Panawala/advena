package com.example.advena.data

import com.example.advena.domain.Event
import com.example.advena.domain.User
import kotlinx.coroutines.flow.Flow

interface IUsersDao {
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
}
