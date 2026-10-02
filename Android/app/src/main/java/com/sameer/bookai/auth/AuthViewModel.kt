package com.sameer.bookai.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sameer.bookai.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    // Indicates whether the session check has been performed
    val isSessionChecked: Boolean = false,
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
                    createBackendSession()
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
                    createBackendSession()
                } else {
                    // Sign Up failed
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "Sign-up failed"
                    )
                }
            }
    }
    // Function to create a backend session after successful Firebase authentication
    private fun createBackendSession() {
        viewModelScope.launch {
            try {
                // Get the currently authenticated user
                val user = auth.currentUser

                if (user == null) {
                    _uiState.value = AuthUiState(
                        isSessionChecked = true,
                        errorMessage = "No authenticated user"
                    )
                    return@launch
                }
                // Get the Firebase ID token for the authenticated user
                val token= user.getIdToken(false).await().token

                if (token == null) {
                    _uiState.value = AuthUiState(
                        isSessionChecked = true,
                        errorMessage = "Failed to get Firebase ID token"
                    )
                    return@launch
                }
                // Make a request to the backend to create a session using the Firebase ID token
                val response = RetrofitInstance.api.createSession(
                    "Bearer $token"
                )

                Log.d(
                    "BookAI",
                    "Backend user: ${response.user.firebaseUid}"
                )

                _uiState.value = AuthUiState(
                    isLoggedIn = true, // Set isLoggedIn to true after successful backend session creation
                    isSessionChecked = true
                )

            } catch (e: Exception) {
                Log.e("BookAI", "Backend session creation failed", e)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSessionChecked = true,
                    errorMessage = "Failed to connect to backend: ${e.message}"
                )
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }

    fun checkSession() {
        val user = auth.currentUser

        if (user == null) {
            _uiState.value = AuthUiState(
                isSessionChecked = true
            )
            return
        }

        _uiState.value = AuthUiState(isLoading = true)

        createBackendSession()
    }

}