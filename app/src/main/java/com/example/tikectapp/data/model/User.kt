package com.example.ticketapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "user", // "user" or "admin"
    @SerialName("created_at")
    val createdAt: String = ""
)

// Role constants
object UserRole {
    const val ADMIN = "admin"
    const val USER = "user"
}

// Admin credentials (hardcoded sesuai requirement)
object AdminCredentials {
    const val USERNAME = "admin"
    const val EMAIL = "admin@ticketapp.com"
    const val PASSWORD = "Admin@12345"
}