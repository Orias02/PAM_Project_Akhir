package com.example.tikectapp.ui.promo


import com.example.tikectapp.data.model.Promo

data class PromoUiState(

    val isLoading: Boolean = false,

    val promos: List<Promo> = emptyList(),

    val errorMessage: String? = null
)