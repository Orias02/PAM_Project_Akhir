package com.example.ticketapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Seat(
    // Kita tidak perlu memasukkan 'val id: String' di sini karena id di-generate otomatis oleh Supabase
    val seat_number: String,
    val is_booked: Boolean,
    val movie_title: String,
    val show_date: String,
    val show_time: String,
    val cinema_location: String
)