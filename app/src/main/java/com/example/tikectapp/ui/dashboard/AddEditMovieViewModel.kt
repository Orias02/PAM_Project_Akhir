package com.example.tikectapp.ui.dashboard


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketapp.data.model.Movie
import com.example.ticketapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

// ── UI State ──────────────────────────────────────────────────────────────────

data class AddEditMovieUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val isDeleteSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isEditMode: Boolean = false,

    // Form fields
    val title: String = "",
    val description: String = "",
    val genre: String = "",
    val rating: String = "",
    val price: String = "",
    val imageUrl: String = "",

    // Field errors
    val titleError: String? = null,
    val descriptionError: String? = null,
    val genreError: String? = null,
    val ratingError: String? = null,
    val priceError: String? = null,
    val imageUrlError: String? = null,
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class AddEditMovieViewModel : ViewModel() {

    private val movieRepository = MovieRepository()

    private val _uiState = MutableStateFlow(AddEditMovieUiState())
    val uiState: StateFlow<AddEditMovieUiState> = _uiState.asStateFlow()

    private var currentMovieId: String? = null

    // ── Load movie untuk mode Edit ────────────────────────────────────────────

    fun loadMovie(movieId: String) {
        currentMovieId = movieId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isEditMode = true)

            movieRepository.getMovieById(movieId).fold(
                onSuccess = { movie ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        title = movie.title,
                        description = movie.description,
                        genre = movie.genre,
                        rating = movie.rating.toString(),
                        price = movie.price.toInt().toString(),
                        imageUrl = movie.imageUrl,
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat data film: ${error.message}"
                    )
                }
            )
        }
    }

    // ── Field update functions ────────────────────────────────────────────────

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value, titleError = null)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value, descriptionError = null)
    }

    fun onGenreChange(value: String) {
        _uiState.value = _uiState.value.copy(genre = value, genreError = null)
    }

    fun onRatingChange(value: String) {
        // Hanya izinkan angka dan titik desimal, max 3 karakter (e.g. "9.9")
        if (value.isEmpty() || value.matches(Regex("^\\d{0,1}(\\.\\d{0,1})?\$"))) {
            _uiState.value = _uiState.value.copy(rating = value, ratingError = null)
        }
    }

    fun onPriceChange(value: String) {
        // Hanya izinkan angka
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(price = value, priceError = null)
        }
    }

    fun onImageUrlChange(value: String) {
        _uiState.value = _uiState.value.copy(imageUrl = value, imageUrlError = null)
    }

    // ── Validasi Form ─────────────────────────────────────────────────────────

    private fun validate(): Boolean {
        val state = _uiState.value
        var isValid = true

        val titleError = when {
            state.title.isBlank() -> "Judul film wajib diisi"
            state.title.length < 2 -> "Judul minimal 2 karakter"
            else -> null
        }

        val descriptionError = when {
            state.description.isBlank() -> "Deskripsi wajib diisi"
            state.description.length < 10 -> "Deskripsi minimal 10 karakter"
            else -> null
        }

        val genreError = if (state.genre.isBlank()) "Genre wajib diisi" else null

        val ratingError = when {
            state.rating.isBlank() -> "Rating wajib diisi"
            state.rating.toFloatOrNull() == null -> "Format rating tidak valid"
            state.rating.toFloat() < 0f || state.rating.toFloat() > 10f -> "Rating harus antara 0 - 10"
            else -> null
        }

        val priceError = when {
            state.price.isBlank() -> "Harga wajib diisi"
            state.price.toLongOrNull() == null -> "Format harga tidak valid"
            state.price.toLong() < 0 -> "Harga tidak boleh negatif"
            else -> null
        }

        val imageUrlError = when {
            state.imageUrl.isBlank() -> "URL gambar wajib diisi"
            !state.imageUrl.startsWith("http") -> "URL harus dimulai dengan http:// atau https://"
            else -> null
        }

        if (titleError != null || descriptionError != null || genreError != null ||
            ratingError != null || priceError != null || imageUrlError != null
        ) {
            isValid = false
        }

        _uiState.value = _uiState.value.copy(
            titleError = titleError,
            descriptionError = descriptionError,
            genreError = genreError,
            ratingError = ratingError,
            priceError = priceError,
            imageUrlError = imageUrlError,
        )

        return isValid
    }

    // ── Save (Add atau Update) ─────────────────────────────────────────────────

    fun saveMovie() {
        if (!validate()) return

        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)

            val movie = Movie(
                id = currentMovieId ?: UUID.randomUUID().toString(),
                title = state.title.trim(),
                description = state.description.trim(),
                genre = state.genre.trim(),
                rating = state.rating.toFloatOrNull() ?: 0f,
                price = state.price.toDoubleOrNull() ?: 0.0,
                imageUrl = state.imageUrl.trim(),
                duration = 0,   // bisa ditambah field nanti
            )

            val result = if (state.isEditMode) {
                movieRepository.updateMovie(movie)
            } else {
                movieRepository.addMovie(movie)
            }

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isSaveSuccess = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Gagal menyimpan film"
                    )
                }
            )
        }
    }

    // ── Delete Film ───────────────────────────────────────────────────────────

    fun deleteMovie() {
        val id = currentMovieId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, errorMessage = null)

            movieRepository.deleteMovie(id).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        isDeleteSuccess = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        errorMessage = error.message ?: "Gagal menghapus film"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}