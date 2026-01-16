package com.example.advena.data

import com.example.advena.domain.Event
import com.example.advena.domain.EventAttendee
import com.example.advena.domain.User
import com.example.advena.domain.UserFollower
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implements IUsersDao interface by interacting with the supabase db.
 */
class UsersDao : IUsersDao {

    private val supabase = SupabaseClient.client

    override fun getAllUsers(): Flow<List<User>> = flow {
        val users = supabase.from("Users")
            .select()
            .decodeList<User>()
        emit(users)
    }

    override suspend fun getUser(uid: String): User {
        return supabase.from("Users")
            .select {
                filter {
                    eq("uid", uid)
                }
            }
            .decodeSingle<User>()
    }

    // Used during login since email (and password) is used to login
    override suspend fun getUserByEmail(email: String): User {
        return supabase.from("Users")
            .select {
                filter {
                    eq("email", email)
                }
            }
            .decodeSingle<User>()
    }

    override suspend fun insertUser(user: User) {
        supabase.from("Users").insert(user)
    }

    override suspend fun updateUser(user: User) {
        supabase.from("Users").update(user) {
            filter {
                eq("uid", user.id)
            }
        }
    }

    override suspend fun deleteUser(user: User) {
        supabase.from("Users").delete {
            filter {
                eq("uid", user.id)
            }
        }
    }

    override suspend fun followUser(followerId: String, followeeId: String) {
        val follow = UserFollower(
            followerId = followerId,
            followeeId = followeeId
        )
        supabase.from("User-Followers").upsert(follow) {
            onConflict = "followerid,followeeid"
            ignoreDuplicates = true
        }
    }

    override suspend fun unfollowUser(followerId: String, followeeId: String) {
        supabase.from("User-Followers").delete {
            filter {
                and {
                    eq("followerid", followerId)
                    eq("followeeid", followeeId)
                }
            }
        }
    }

    // This user is a followee of other users
    // Join between User-Followers and Users
    override fun getUserFollowers(uid: String): Flow<List<User>> = flow {
        val followerRelations = supabase.from("User-Followers")
            .select {
                filter {
                    eq("followeeid", uid)
                }
            }
            .decodeList<UserFollower>()

        val followerIds = followerRelations.map { it.followerId }

        // Now get the following users
        if (followerIds.isEmpty()) {
            emit(emptyList())
        } else {
            val followers = supabase.from("Users")
                .select {
                    filter {
                        isIn("uid", followerIds)
                    }
                }
                .decodeList<User>()
            emit(followers)
        }
    }

    // This user is a follower of other users
    // Join between User-Followers and Users
    override fun getUserFollowing(uid: String): Flow<List<User>> = flow {
        val followingRelations = supabase.from("User-Followers")
            .select {
                filter {
                    eq("followerid", uid)
                }
            }
            .decodeList<UserFollower>()

        val followeeIds = followingRelations.map { it.followeeId }

        // Now get the followed users
        if (followeeIds.isEmpty()) {
            emit(emptyList())
        } else {
            val following = supabase.from("Users")
                .select {
                    filter {
                        isIn("uid", followeeIds)
                    }
                }
                .decodeList<User>()
            emit(following)
        }
    }

    override fun getEventsHostedByUser(uid: String): Flow<List<Event>> = flow {
        val events = supabase.from("Events")
            .select {
                filter {
                    eq("host_id", uid)
                }
            }
            .decodeList<Event>()
        emit(events)
    }

    // Join between Event-Attendees and Events
    override fun getEventsAttendedByUser(uid: String): Flow<List<Event>> = flow {
        val attendanceRecords = supabase.from("Event-Attendees")
            .select {
                filter {
                    eq("uid", uid)
                }
            }
            .decodeList<EventAttendee>()

        val eventIds = attendanceRecords.map { it.eventId }

        // Now get the actual events
        if (eventIds.isEmpty()) {
            emit(emptyList())
        } else {
            val events = supabase.from("Events")
                .select {
                    filter {
                        isIn("eid", eventIds)
                    }
                }
                .decodeList<Event>()
            emit(events)
        }
    }
}
