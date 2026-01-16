package com.example.advena.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserFollower(
    @SerialName("followerid") var followerId: String,
    @SerialName("followeeid") var followeeId: String,
)