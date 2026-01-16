package com.example.advena.data

import com.example.advena.domain.Event
import com.example.advena.domain.EventType
import com.example.advena.domain.Location
import com.example.advena.domain.User
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

object SharedMockStorage {
    val instance: MockStorage by lazy {
        val storage = MockStorage()
        runBlocking {  initializeMockData(storage) }
        storage
    }

    private suspend fun initializeMockData(storage: MockStorage) {
        // Users
        val user0 = User(
            id = "EltonQ11",
            name = "Elton Quin",
            email = "eltonq11@example.com",
            bio = "This is my page! Look at all the events I've been apart of"
        )

        val user1 = User(
            id = "SarahJ22",
            name = "Sarah Johnson",
            email = "sarah.j@example.com",
            bio = "This is my page! Look at all the events I've been apart of"
        )

        val user2 = User(
            id = "MikeC88",
            name = "Mike Chen",
            email = "345-678-9012"
        )

        val user3 = User(id = "EmilyR15", name = "Emily Rodriguez", email = "emily.r@example.com")
        val user4 = User(id = "AlexK77", name = "Alex Kim", email = "alex.kim@example.com")
        val user5 = User(id = "JessicaM44", name = "Jessica Martinez", email = "jessica.m@example.com")

        val user6 = User(id = "LockedInDave6", name = "Dave Locke", email = "dave.locke@example.com")
        val user7 = User(id = "DaveBurns3", name = "Dave Burns", email = "dave.burns@example.com")
        val user8 = User(id = "DaveTheMan99", name = "David Morgan", email = "david.morgan@example.com")
        val user9 = User(id = "BoomDave40", name = "Dave", email = "dave@example.com")

        val allUsers = listOf(
            user0, user1, user2, user3, user4, user5, user6, user7, user8, user9
        )

        allUsers.forEach { storage.insertUser(it) }
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        val event1 = Event(
            id = "event_001",
            name = "Board Game Night",
            description = "Join us for an evening of strategy games and fun!",
            hostId = "EltonQ11",
            address = "123 Main St, Toronto, ON",
            longitude = -79.3832,
            latitude = 43.6532,
            date = today.toString(),
            startTime = "19:00",
            endTime = "23:00",
            estimatedCost = 15.0,
            maxAttendees = 8,
            tags = "games,indoor,casual",
            type = EventType.PUBLIC
        )

        val event2 = Event(
            id = "event_002",
            name = "Coffee Meetup",
            description = "Let's catch up over coffee!",
            hostId = "EltonQ11",
            address = "456 Queen St, Toronto, ON",
            longitude = -79.3871,
            latitude = 43.6426,
            date = today.toString(),
            startTime = "10:00",
            endTime = "12:00",
            estimatedCost = 8.0,
            maxAttendees = 6,
            tags = "coffee,casual,social",
            type = EventType.PUBLIC
        )

        val event3 = Event(
            id = "event_003",
            name = "Movie Night",
            description = "Watching the latest blockbuster!",
            hostId = "SarahJ22",
            address = "789 King St, Toronto, ON",
            longitude = -79.3798,
            latitude = 43.6481,
            date = today.plusDays(3).toString(),
            startTime = "20:00",
            endTime = "23:00",
            estimatedCost = 20.0,
            maxAttendees = 10,
            tags = "movies,indoor,entertainment",
            type = EventType.PUBLIC
        )

        val event4 = Event(
            id = "event_004",
            name = "Park Picnic",
            description = "Outdoor lunch in the park",
            hostId = "MikeC88",
            address = "High Park, Toronto, ON",
            longitude = -79.4637,
            latitude = 43.6465,
            date = today.plusDays(10).toString(),
            startTime = "12:00",
            endTime = "15:00",
            estimatedCost = 0.0,
            maxAttendees = 15,
            tags = "outdoor,picnic,nature",
            type = EventType.PUBLIC
        )

        val event5 = Event(
            id = "event_005",
            name = "Karaoke Night",
            description = "Sing your heart out!",
            hostId = "SarahJ22",
            address = "321 Dundas St, Toronto, ON",
            longitude = -79.3802,
            latitude = 43.6555,
            date = today.toString(),
            startTime = "21:00",
            endTime = "01:00",
            estimatedCost = 25.0,
            maxAttendees = 12,
            tags = "music,entertainment,nightlife",
            type = EventType.PUBLIC
        )

        listOf<Event>(event1, event2, event3, event4, event5).forEach { storage.insertEvent(it) }

        listOf(
            Event(
                id = "1",
                name = "Student Social Mixer",
                description = "Meet fellow students at the Student Life Centre",
                hostId = "AlexK77",
                address = "200 University Ave W, Waterloo, ON",
                latitude = 43.4719,  // UW Student Life Centre
                longitude = -80.5450,
                date = today.toString(), // Today
                startTime = "14:00",
                endTime = "22:00",
                estimatedCost = 0.0,
                maxAttendees = 1,
                tags = "Social,Student Life,Networking",
                type = EventType.PUBLIC
            ),
            Event(
                id = "2",
                name = "Tech Career Fair",
                description = "Meet top tech companies at E7",
                hostId = "BoomDave40",
                address = "E7, University of Waterloo",
                latitude = 43.4728,  // Engineering 7 building
                longitude = -80.5397,
                date = today.toString(), // Today
                startTime = "10:00",
                endTime = "16:00",
                estimatedCost = 10.00,
                maxAttendees = 500,
                tags = "Career,Tech,Networking",
                type = EventType.PUBLIC
            ),
            Event(
                id = "3",
                name = "Shopping Night",
                description = "Late night shopping event at Conestoga Mall with special discounts",
                hostId = "JessicaM44",
                address = "550 King St N, Waterloo, ON",
                latitude = 43.4980,  // Conestoga Mall
                longitude = -80.5285,
                date = tomorrow.toString(), // Tomorrow
                startTime = "18:00",
                endTime = "23:00",
                estimatedCost = 35.00,
                maxAttendees = 1000,
                tags = "Shopping,Social,Night Life",
                type = EventType.PUBLIC
            ),
            Event(
                id = "4",
                name = "Carnival Street Festival",
                description = "Experience authentic Brazilian culture with samba dancing, traditional food, and live music at Copacabana Beach",
                hostId = "MikeC88",
                address = "Copacabana Beach, Rio de Janeiro, Brazil",
                latitude = -22.9711,  // Copacabana Beach, Rio de Janeiro
                longitude = -43.1822,
                date = today.toString(), // Today
                startTime = "16:00",
                endTime = "23:00",
                estimatedCost = 25.00,
                maxAttendees = 2000,
                tags = "Cultural,Music,Dance,Food,Beach,Festival",
                type = EventType.PUBLIC
            )
        ).forEach { storage.insertEvent(it) }

        // Friends
        storage.followUser("EltonQ11","SarahJ22")
        storage.followUser("EltonQ11","MikeC88")
        storage.followUser("SarahJ22", "EltonQ11")
        storage.followUser("MikeC88", "EltonQ11")

        // Attended events
        storage.attendEvent("EltonQ11", "event_003")
        storage.attendEvent("EltonQ11", "event_004")
        storage.attendEvent("EltonQ11", "event_005")
        storage.attendEvent("SarahJ22", "1")
    }
}
