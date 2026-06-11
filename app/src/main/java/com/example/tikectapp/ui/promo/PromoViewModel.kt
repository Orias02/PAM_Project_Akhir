package com.example.tikectapp.ui.promo


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tikectapp.data.repository.PromoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PromoViewModel : ViewModel() {

    private val repository = PromoRepository()

    private val _uiState = MutableStateFlow(
        PromoUiState(
            isLoading = true
        )
    )

    val uiState: StateFlow<PromoUiState> =
        _uiState.asStateFlow()

    init {
        loadPromos()
    }

    fun loadPromos() {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true
            )

            val result = repository.getAllPromos()

            result.fold(

                onSuccess = { promos ->

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        promos = promos
                    )
                },

                onFailure = { error ->

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            )
        }
    }
}