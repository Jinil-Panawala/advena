package com.example.advena.domain

import com.example.advena.data.MockStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ModelUserTest {

    private lateinit var model: Model
    private lateinit var storage: MockStorage

    @Before
    fun setup() = runBlocking {
        storage = MockStorage()
        model = Model(storage)
    }

    //test to create user and verify it exists
    @Test
    fun addUserWithUniqueId() = runBlocking {
        model.createUser("1", "bob", "bob@test.com")

        val user = model.getUser("1")
        assert(user.id == "1")
        assert(user.name == "bob")
    }

    //test that allows you to add user with same id (overwrites in storage)
    @Test
    fun addUserWithSameId() = runBlocking {
        model.createUser("1", "bob", "bob@test.com")
        model.createUser("1", "alice", "alice@test.com")

        val allUsers = model.allUsers.first()
        // Should only have one user with id "1"
        assert(allUsers.count { it.id == "1" } == 1)

        val user = model.getUser("1")
        assert(user.name == "alice") // Second user overwrites first
    }

    //test should create user and add them to the storage
    @Test
    fun createUser() = runBlocking {
        model.createUser("2", "Bob", "bob@email.com")

        val user = model.getUser("2")
        assert(user.id == "2")
        assert(user.name == "Bob")
        assert(user.email == "bob@email.com")
    }

    //test to get all users
    @Test
    fun getAllUsers() = runBlocking {
        model.createUser("u1", "Alice", "alice@uwaterloo.ca")
        model.createUser("u2", "Bob", "bob@gmail.com")

        val allUsers = model.allUsers.first()
        assert(allUsers.size == 2)
        assert(allUsers.any { it.id == "u1" && it.name == "Alice" })
        assert(allUsers.any { it.id == "u2" && it.name == "Bob" })
    }

    //add user as follower (follow functionality)
    @Test
    fun followUser() = runBlocking {
        model.createUser("u1", "Alice", "alice@uwaterloo.ca")
        model.createUser("u2", "Bob", "bob@gmail.com")

        model.followUser("u1", "u2") // u1 follows u2

        val u1Following = model.getUserFollowing("u1").first()
        val u2Followers = model.getUserFollowers("u2").first()

        assert(u1Following.any { it.id == "u2" })
        assert(u2Followers.any { it.id == "u1" })
    }

    // attempt to add duplicate follow
    @Test
    fun followUserDuplicate() = runBlocking {
        model.createUser("u1", "Alice", "alice@uwaterloo.ca")
        model.createUser("u2", "Bob", "bob@gmail.com")

        model.followUser("u1", "u2")
        model.followUser("u1", "u2") // Duplicate follow

        val u1Following = model.getUserFollowing("u1").first()
        // Should still only have one follow relationship
        assert(u1Following.count { it.id == "u2" } == 1)
    }


    // unfollow user
    @Test
    fun unfollowUser() = runBlocking {
        model.createUser("u1", "Alice", "alice@uwaterloo.ca")
        model.createUser("u2", "Bob", "bob@gmail.com")

        model.followUser("u1", "u2")
        var u1Following = model.getUserFollowing("u1").first()
        assert(u1Following.any { it.id == "u2" })

        model.unfollowUser("u1", "u2")
        u1Following = model.getUserFollowing("u1").first()
        assert(u1Following.none { it.id == "u2" })
    }

    // unfollow when not following (should be no-op)
    @Test
    fun unfollowWhenNotFollowing() = runBlocking {
        model.createUser("u1", "Alice", "alice@uwaterloo.ca")
        model.createUser("u2", "Bob", "bob@gmail.com")

        model.unfollowUser("u1", "u2") // Not following, should be no-op

        val u1Following = model.getUserFollowing("u1").first()
        assert(u1Following.isEmpty())
    }

    // remove user and delete them as host of an event
    @Test
    fun removeUserRemovesAssociations() = runBlocking {
        model.createUser("u1", "Alice", "alice@test.com")
        model.createUser("u2", "Bob", "bob@test.com")
        model.createUser("u3", "Charlie", "charlie@test.com")

        model.followUser("u1", "u2")
        model.followUser("u1", "u3")

        model.createEvent(
            "e1",
            "Board Games",
            "Fun evening of Catan and Uno",
            "u1",
            "123 King St",
            43.4723,
            -80.5449,
            "2025-10-20",
            "18:00",
            "22:00",
            "games,fun",
            0.0,
            5,
            type = EventType.PUBLIC
        )

        model.attendEvent("u2", "e1")

        // Verify initial state
        val user1 = model.getUser("u1")
        assert(user1.id == "u1")

        val u1Following = model.getUserFollowing("u1").first()
        assert(u1Following.size == 2)

        val hostedEvents = model.getEventsHostedByUser("u1").first()
        assert(hostedEvents.any { it.id == "e1" })

        val u2AttendedEvents = model.getEventsAttendedByUser("u2").first()
        assert(u2AttendedEvents.any { it.id == "e1" })

        // Remove user u1
        model.deleteUser(user1)

        // Verify user is deleted
        try {
            model.getUser("u1")
            assert(false) // Should throw exception
        } catch (e: Exception) {
            // Expected
        }

        // Verify follow relationships are removed
        val u2Followers = model.getUserFollowers("u2").first()
        val u3Followers = model.getUserFollowers("u3").first()
        assert(u2Followers.none { it.id == "u1" })
        assert(u3Followers.none { it.id == "u1" })

        // Verify hosted events are removed
        val allEvents = model.allEvents.first()
        assert(allEvents.none { it.id == "e1" })

        // Verify attendee relationships are removed
        val u2AttendedAfter = model.getEventsAttendedByUser("u2").first()
        assert(u2AttendedAfter.none { it.id == "e1" })
    }

    //attempt to remove non-existent user (will throw exception or be no-op depending on implementation)
    @Test
    fun removeNonexistentUser() = runBlocking {
        val allUsersBefore = model.allUsers.first()
        assert(allUsersBefore.isEmpty())

        // This user does not exist in storage
        val ghostUser = User("ghostUser", "Ghost", "ghost@email.com", "bio")
        // Attempting to delete non-existent user
        // This may throw exception or be no-op
        try {
            model.deleteUser(ghostUser)
        } catch (e: Exception) {
            // May throw exception
        }

        val allUsersAfter = model.allUsers.first()
        assert(allUsersAfter.isEmpty())
    }

    //update the existing user's information
    @Test
    fun updateExistingUser() = runBlocking {
        model.createUser("u1", "Alice", "alice@uwaterloo.ca")

        var user = model.getUser("u1")
        assertEquals(user.name, "Alice")
        assertEquals(user.email, "alice@uwaterloo.ca")

        val updatedUser = User("u1", "john", "john@uwaterloo.ca")
        model.updateUser(updatedUser)

        user = model.getUser("u1")
        assertEquals(user.name, "john")
        assertEquals(user.email, "john@uwaterloo.ca")
    }

    //attempt to change user's information when user id not valid (will create new user)
    @Test
    fun updateNonExistentUser() = runBlocking {
        model.createUser("u1", "Alice", "alice@uwaterloo.ca")

        var user = model.getUser("u1")
        assertEquals(user.name, "Alice")

        // Update with different ID - this will create a new user in storage
        val user2 = User("u3", "john", "john@uwaterloo.ca")
        model.updateUser(user2)

        // Original user unchanged
        user = model.getUser("u1")
        assertEquals(user.name, "Alice")

        // New user created
        val newUser = model.getUser("u3")
        assertEquals(newUser.name, "john")
    }

    // Test getting followers
    @Test
    fun getUserFollowers() = runBlocking {
        model.createUser("u1", "Alice", "alice@uwaterloo.ca")
        model.createUser("u2", "Bob", "bob@gmail.com")
        model.createUser("u3", "Charlie", "charlie@hotmail.ca")

        // u2 and u3 follow u1
        model.followUser("u2", "u1")
        model.followUser("u3", "u1")

        val u1Followers = model.getUserFollowers("u1").first()
        assert(u1Followers.size == 2)
        assert(u1Followers.any { it.id == "u2" })
        assert(u1Followers.any { it.id == "u3" })
    }

    // Test getting following
    @Test
    fun getUserFollowing() = runBlocking {
        model.createUser("u1", "Alice", "alice@uwaterloo.ca")
        model.createUser("u2", "Bob", "bob@gmail.com")
        model.createUser("u3", "Charlie", "charlie@hotmail.ca")

        // u1 follows u2 and u3
        model.followUser("u1", "u2")
        model.followUser("u1", "u3")

        val u1Following = model.getUserFollowing("u1").first()
        assert(u1Following.size == 2)
        assert(u1Following.any { it.id == "u2" })
        assert(u1Following.any { it.id == "u3" })
    }

    // Test user with no followers
    @Test
    fun getUserWithNoFollowers() = runBlocking {
        model.createUser("u1", "Alice", "alice@uwaterloo.ca")

        val u1Followers = model.getUserFollowers("u1").first()
        assert(u1Followers.isEmpty())
    }

    // Test user following no one
    @Test
    fun getUserFollowingNoOne() = runBlocking {
        model.createUser("u1", "Alice", "alice@uwaterloo.ca")

        val u1Following = model.getUserFollowing("u1").first()
        assert(u1Following.isEmpty())
    }
}

