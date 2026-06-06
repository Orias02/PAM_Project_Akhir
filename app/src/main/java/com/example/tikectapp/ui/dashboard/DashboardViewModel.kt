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

    // semua movie dari database
    val allMovies: List<Movie> = emptyList(),

    // movie yang tampil di dashboard
    val movies: List<Movie> = emptyList(),

    val currentUser: UserProfile? = null,

    val errorMessage: String? = null,

    // checker admin
    val isAdmin: Boolean = false,

    // lokasi default
    val selectedLocation: String = "Semua"
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

    // =========================
    // LOAD USER
    // =========================

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

                isAdmin = user.role == UserRole.ADMIN
            )
        }
    }

    // =========================
    // LOAD MOVIES
    // =========================

    fun loadMovies() {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true
            )

            val result = movieRepository.getAllMovies()

            result.fold(

                onSuccess = { movies ->

                    val selectedLocation =
                        _uiState.value.selectedLocation

                    val filteredMovies =

                        if (selectedLocation == "Semua") {

                            // tampilkan semua film
                            movies

                        } else {

                            // filter berdasarkan lokasi
                            movies.filter { movie ->

                                movie.locations.contains(
                                    selectedLocation
                                )
                            }
                        }

                    _uiState.value = _uiState.value.copy(

                        isLoading = false,

                        // simpan semua movie
                        allMovies = movies,

                        // movie hasil filter
                        movies = filteredMovies
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

    // =========================
    // CHANGE LOCATION
    // =========================

    fun changeLocation(location: String) {

        val filteredMovies =

            if (location == "Semua") {

                // tampilkan semua film
                _uiState.value.allMovies

            } else {

                // filter movie berdasarkan lokasi
                _uiState.value.allMovies.filter { movie ->

                    movie.locations.contains(location)
                }
            }

        _uiState.value = _uiState.value.copy(

            selectedLocation = location,

            movies = filteredMovies
        )
    }

    // =========================
    // CHECK ADMIN
    // =========================

    fun isAdmin(): Boolean {

        return _uiState.value.isAdmin
    }
}