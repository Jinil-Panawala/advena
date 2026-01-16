package com.example.advena.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
    * Enum class representing different types of events.
 */

@Serializable
enum class EventType {
    @SerialName("PUBLIC") PUBLIC,
    @SerialName("FOLLOWER") FOLLOWER,
    @SerialName("FRIEND") FRIEND,
}
