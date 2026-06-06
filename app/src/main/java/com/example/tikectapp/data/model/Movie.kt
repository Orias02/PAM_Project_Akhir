package com.example.ticketapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(

    val id: String = "",
    val title: String = "",
    val description: String = "",
    val genre: String = "",
    val rating: Float = 0f,
    val duration: Int = 0, // in minutes
    val price: Double = 0.0,

    @SerialName("image_url")
    val imageUrl: String = "",

    @SerialName("release_date")
    val releaseDate: String = "",

    @SerialName("is_3d")
    val is3D: Boolean = false,

    // =========================
    // MULTI LOCATION BIOSKOP
    // =========================
    val locations: List<String> = emptyList(),

    @SerialName("created_at")
    val createdAt: String = ""
)

// =========================
// GENRE FILM
// =========================
object MovieGenre {

    const val ALL = "All"
    const val ACTION = "Action"
    const val DRAMA = "Drama"
    const val COMEDY = "Comedy"
    const val HORROR = "Horror"
    const val ANIMATION = "Animation"
    const val DOCUMENTARY = "Documentary"
    const val BAPAK = "Bapak"

    val allGenres = listOf(
        ALL,
        ACTION,
        DRAMA,
        COMEDY,
        HORROR,
        ANIMATION,
        DOCUMENTARY
    )
}

// =========================
// LOKASI BIOSKOP
// =========================
object CinemaLocation {

    const val MOG = "Malang Town Square"
    const val CYBER = "Cyber Mall"
    const val DIENG = "Dieng Plaza"

    val allLocations = listOf(
        MOG,
        CYBER,
        DIENG
    )
}