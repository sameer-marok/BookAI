package com.sameer.bookai.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // Function to set validation error message
    fun setValidationError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
    }

    // Function to handle user sign-in
    fun signIn(
        email: String,
        password: String
    ) {
        _uiState.value = AuthUiState(isLoading = true)
        // Use FirebaseAuth to sign in with email and password
        auth.signInWithEmailAndPassword(email, password)
            // Add a completion listener to handle the result of the sign-in operation
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    _uiState.value = AuthUiState(isLoggedIn = true)
                } else {
                    // Login failed
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "Sign-in failed"
                    )
                }
            }
    }

    fun signUp(
        email: String,
        password: String
    ) {
        _uiState.value = AuthUiState(isLoading = true)
        // Creating new user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign Up successful
                    _uiState.value = AuthUiState(isLoggedIn = true)
                } else {
                    // Sign Up failed
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "Sign-up failed"
                    )
                }
            }
    }

}