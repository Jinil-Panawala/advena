package com.example.advena.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponse(
    val results: List<Result>,
    val status: String
)

@Serializable
data class Result(
    val formatted_address: String,
    val geometry: Geometry
)

@Serializable
data class Geometry(
    val location: LocationResponse
)

@Serializable
data class LocationResponse(
    val lat: Double,
    val lng: Double
)
