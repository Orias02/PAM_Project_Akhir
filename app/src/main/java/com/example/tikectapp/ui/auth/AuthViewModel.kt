package com.example.ticketapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketapp.data.model.UserProfile
import com.example.ticketapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────────

data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: UserProfile? = null,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false,
    val isRegisterSuccess: Boolean = false
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Form fields state
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    // ── Init: cek session aktif ───────────────────────────────────────────────

    init {
        checkCurrentSession()
    }

    private fun checkCurrentSession() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    isLoginSuccess = true
                )
            }
        }
    }

    // ── Field update functions ────────────────────────────────────────────────

    fun onUsernameChange(value: String) { _username.value = value }
    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun onConfirmPasswordChange(value: String) { _confirmPassword.value = value }

    // ── Sign In ───────────────────────────────────────────────────────────────

    fun signIn() {
        val usernameVal = _username.value.trim()
        val passwordVal = _password.value

        // Validasi input
        if (usernameVal.isEmpty() || passwordVal.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Username dan password wajib diisi"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.signIn(usernameVal, passwordVal)

            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = user,
                        isLoginSuccess = true,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login gagal"
                    )
                }
            )
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────

    fun register() {
        val emailVal = _email.value.trim()
        val usernameVal = _username.value.trim()
        val passwordVal = _password.value

        // Validasi input
        when {
            emailVal.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Email wajib diisi")
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(emailVal).matches() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Format email tidak valid")
                return
            }
            usernameVal.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Username wajib diisi")
                return
            }
            usernameVal.length < 3 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Username minimal 3 karakter")
                return
            }
            passwordVal.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Password wajib diisi")
                return
            }
            passwordVal.length < 6 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Password minimal 6 karakter")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.register(emailVal, usernameVal, passwordVal)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegisterSuccess = true,
                        errorMessage = null
                    )
                    clearFields()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Registrasi gagal"
                    )
                }
            )
        }
    }

    // ── Sign Out ──────────────────────────────────────────────────────────────

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState()
            clearFields()
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearRegisterSuccess() {
        _uiState.value = _uiState.value.copy(isRegisterSuccess = false)
    }

    private fun clearFields() {
        _username.value = ""
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
    }
}