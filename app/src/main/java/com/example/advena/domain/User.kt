package com.example.advena.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * class representing a user.
 *
 * @property id Unique identifier for the user.
 * @property name Full name of the user.
 * @property email Email address (used for login/communication).
 * @property bio Biography (user description)
 */

@Serializable
data class User (
    @SerialName("uid") val id: String,
    @SerialName("name") var name: String,
    @SerialName("email") var email: String? = null,
    @SerialName("bio") var bio: String? = null
)
