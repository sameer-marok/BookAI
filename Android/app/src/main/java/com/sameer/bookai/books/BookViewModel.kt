package com.sameer.bookai.books

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sameer.bookai.data.createPdfPart
import com.sameer.bookai.data.remote.RetrofitInstance
import com.sameer.bookai.data.remote.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

// UI state for the BookViewModel
data class BookUiState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val errorMessage: String? = null,
    val uploadSuccess: Boolean = false
)

class BookViewModel: ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState

    // Initialize the ViewModel and load books when it's created
    init {
        loadBooks()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
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

    fun uploadBook(
        context: Context,
        uri: Uri,
        title: String
    ) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                uploadSuccess = false
            )

            try {
                val user = auth.currentUser

                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User is not authenticated",
                        uploadSuccess = false
                    )
                    return@launch
                }

                val token = user.getIdToken(false).await().token

                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to get Firebase ID token",
                        uploadSuccess = false
                    )
                    return@launch
                }

                // Prepare the PDF for multipart upload
                // Use IO dispatcher for file operations to avoid blocking the main thread
                val preparedUpload = withContext(Dispatchers.IO) {
                    createPdfPart(context, uri)
                }

                // Upload the book using the prepared multipart data
                try {
                    // Create the title form field
                    val titlePart = title.toRequestBody("text/plain".toMediaType())

                    val newBook = RetrofitInstance.api.uploadBook(
                        token = "Bearer $token",
                        title = titlePart,
                        file = preparedUpload.filePart
                    )

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        books = _uiState.value.books + newBook,
                        errorMessage = null,
                        uploadSuccess = true
                    )
                } finally {
                    // Delete the temporary file after the upload is complete
                    preparedUpload.tempFile.delete()
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to upload book",
                    uploadSuccess = false
                )
            }
        }
    }

}