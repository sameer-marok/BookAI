package com.sameer.bookai.books

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sameer.bookai.data.remote.RetrofitInstance
import com.sameer.bookai.data.remote.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// UI state for the BookViewModel
data class BookUiState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val errorMessage: String? = null
)

class BookViewModel: ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState

    // Initialize the ViewModel and load books when it's created
    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = BookUiState(isLoading = true)

            try {
                val user = auth.currentUser

                if (user == null) {
                    _uiState.value = BookUiState(
                        errorMessage = "User is not authenticated"
                    )
                    return@launch
                }

                val token = user.getIdToken(false).await().token

                if (token == null) {
                    _uiState.value = BookUiState(
                        errorMessage = "Failed to get Firebase ID token"
                    )
                    return@launch
                }

                val books = RetrofitInstance.api.getBooks(
                    "Bearer $token"
                )

                _uiState.value = BookUiState(
                    books = books
                )

            } catch (e: Exception) {
                _uiState.value = BookUiState(
                    errorMessage = e.message ?: "Failed to load books"
                )
            }
        }
    }

}