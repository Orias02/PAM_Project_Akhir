package com.example.ticketapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketapp.data.model.AdminCredentials
import com.example.ticketapp.data.model.Movie
import com.example.ticketapp.data.model.UserProfile
import com.example.ticketapp.data.model.UserRole
import com.example.ticketapp.data.repository.AuthRepository
import com.example.ticketapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val currentUser: UserProfile? = null,
    val errorMessage: String? = null,

    // penting buat tombol admin
    val isAdmin: Boolean = false
)

class DashboardViewModel : ViewModel() {

    private val movieRepository = MovieRepository()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            isLoading = true
        )
    )

    val uiState: StateFlow<DashboardUiState> =
        _uiState.asStateFlow()

    init {
        loadCurrentUser()
        loadMovies()
    }

    private fun loadCurrentUser() {

        viewModelScope.launch {

            var user = authRepository.getCurrentUser()

            // fallback admin hardcoded
            if (user == null) {

                user = UserProfile(
                    id = "admin-id",
                    username = AdminCredentials.USERNAME,
                    email = AdminCredentials.EMAIL,
                    role = UserRole.ADMIN
                )
            }

            _uiState.value = _uiState.value.copy(
                currentUser = user,

                // INI YANG BIKIN LANGSUNG UPDATE
                isAdmin = user.role == UserRole.ADMIN
            )
        }
    }

    fun loadMovies() {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true
            )

            val result = movieRepository.getAllMovies()

            result.fold(

                onSuccess = { movies ->

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        movies = movies
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

    fun isAdmin(): Boolean {

        return _uiState.value.isAdmin
    }
}