package com.example.advena.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class EventAttendee (
    @SerialName("eid") var eventId: String,
    @SerialName("uid") var userId: String
)