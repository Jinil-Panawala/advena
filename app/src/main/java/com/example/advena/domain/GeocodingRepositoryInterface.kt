package com.example.advena.domain

interface GeocodingRepositoryInterface {
    suspend fun geocode(address: String): Location?
}