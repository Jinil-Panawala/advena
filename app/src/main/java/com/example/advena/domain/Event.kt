package com.example.advena.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * class representing an event.
 *
 * @property id Unique identifier for the event.
 * @property name Name of the event.
 * @property description Detailed description of the event.
 * @property hostId User ID of the event host.
 * @property address Address of event.
 * @property date Date of the event in "YYYY-MM-DD" format.
 * @property startTime Start time of the event in "HH:MM" format.
 * @property endTime End time of the event in "HH:MM" format.
 * @property estimatedCost Estimated cost to attend the event.
 * @property maxAttendees Maximum number of attendees allowed.
 * @property tags  Tags (comma separated) associated with the event (e.g., "hiking, outdoors").
 * @property type Type of event (e.g., "public", "follower","friend").
 */

@Serializable
data class Event(
    @SerialName("eid") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") var description: String,
    @SerialName("host_id") val hostId: String,
    @SerialName("address") val address: String,
    @SerialName("longitude") val longitude: Double,
    @SerialName("latitude") val latitude: Double,
    @SerialName("date") var date: String,
    @SerialName("start_time") var startTime: String,
    @SerialName("end_time") var endTime: String,
    @SerialName("estimated_cost") val estimatedCost: Double,
    @SerialName("max_attendees") val maxAttendees: Int,
    @SerialName("tags") val tags: String,
    @SerialName("type") val type: EventType,
)