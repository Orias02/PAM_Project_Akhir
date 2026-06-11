package com.example.tikectapp.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
    data class Promo(
    val id: String = "",

    val title: String = "",

    val description: String = "",

    @SerialName("voucher_code")
    val voucherCode: String = "",

    @SerialName("banner_url")
    val bannerUrl: String = "",

    @SerialName("is_active")
    val isActive: Boolean = true,

    @SerialName("created_at")
    val createdAt: String? = null
)