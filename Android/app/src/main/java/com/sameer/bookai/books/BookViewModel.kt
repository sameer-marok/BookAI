package com.sameer.bookai.books

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sameer.bookai.data.remote.RetrofitInstance
import com.sameer.bookai.data.remote.model.Book
import com.sameer.bookai.data.remote.model.CreateBookRequest
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
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val user = auth.currentUser

                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User is not authenticated"
                    )
                    return@launch
                }

                val token = user.getIdToken(false).await().token

                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
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
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load books"
                )
            }
        }
    }

    fun createBook(title: String) {
        viewModelScope.launch {
            // using copy to update the existing state while keeping other properties intact
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val user = auth.currentUser

                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User is not authenticated"
                    )
                    return@launch
                }

                val token = user.getIdToken(false).await().token

                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to get Firebase ID token"
                    )
                    return@launch
                }

                val newBook = RetrofitInstance.api.createBook(
                    "Bearer $token",
                    CreateBookRequest(title)
                )

                // Update the UI state with the newly created book
                val updatedBooks = _uiState.value.books + newBook
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    books = updatedBooks,
                    errorMessage = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to create book"
                )
            }
        }
    }

}