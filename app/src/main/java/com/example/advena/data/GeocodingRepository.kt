package com.example.advena.data

import com.example.advena.domain.Location
import com.example.advena.domain.GeocodingRepositoryInterface
import com.example.advena.data.remote.GeocodingService

class GeocodingRepository (
    private val service: GeocodingService
) : GeocodingRepositoryInterface {
    override suspend fun geocode(address: String): Location? {
        val response = service.geocode(address)
        val result = response.results.firstOrNull() ?: return null
        return Location(
            address = result.formatted_address,
            latitude = result.geometry.location.lat,
            longitude = result.geometry.location.lng,
        )
    }
}
