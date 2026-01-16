package com.example.advena.data.remote

import com.example.advena.data.ApiKeys
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class GeocodingService(
    private val apiKey: String = ApiKeys.GOOGLE_GEOCODING_API_KEY,
    private val client: HttpClient
) {
    suspend fun geocode(address: String): GeocodingResponse {
        return client.get("https://maps.googleapis.com/maps/api/geocode/json") {
            parameter("address", address)
            parameter("key", apiKey)
            accept(ContentType.Application.Json)
        }.body()
    }

}