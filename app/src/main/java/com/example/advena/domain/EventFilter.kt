package com.example.advena.domain

sealed class EventFilter {
    data class ByLocation(val longitude: Double, val latitude: Double, val radiusKm: Double) : EventFilter()
    data class ByTag(val tag: String) : EventFilter()
    data class ByAddress(val query: String) : EventFilter()
    data class ByDateRange(val startDate: String, val endDate: String) : EventFilter()
    data class ByMaxAttendees(val maxAttendees: Int) : EventFilter()
    data class ByCost(val maxCost: Double) : EventFilter()
    data class ByHostedBy(val userId: String) : EventFilter()
    data class Attending(val userId: String) : EventFilter()
}