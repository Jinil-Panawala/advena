package com.example.advena.domain

/**
 * Data class representing a geographical location.
 *
 * @property latitude The latitude of the location.
 * @property longitude The longitude of the location.
 * @property address An optional human-readable address for the location.
 */

data class Location(
    val address: String,  //for human-readable address
    val longitude: Double,
    val latitude: Double,
)
