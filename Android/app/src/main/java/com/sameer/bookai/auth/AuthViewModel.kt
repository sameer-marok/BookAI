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

    fun testBackend() {
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser

                if (user == null) {
                    Log.e("BookAI", "No authenticated user")
                    return@launch
                }

                val token = user.getIdToken(false).await().token

                if (token == null) {
                    Log.e("BookAI", "Failed to get Firebase ID token")
                    return@launch
                }

                val response = RetrofitInstance.api.getProtected(
                    "Bearer $token"
                )

                Log.d("BookAI", "Backend response: ${response.message}")
                Log.d("BookAI", "Backend user: ${response.user.uid}")

            } catch (e: Exception) {
                Log.e("BookAI", "Backend request failed", e)
            }
        }
    }

}