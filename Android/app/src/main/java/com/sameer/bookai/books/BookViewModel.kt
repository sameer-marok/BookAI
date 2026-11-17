package com.sameer.bookai.books

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sameer.bookai.data.createPdfUploadFile
import com.sameer.bookai.data.remote.RetrofitInstance
import com.sameer.bookai.data.remote.model.Book
import com.sameer.bookai.data.remote.model.ConfirmUploadRequest
import com.sameer.bookai.data.remote.model.CreateUploadUrlRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

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
            // Temporary file to hold the PDF during upload
            var tempFile: File? = null

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

                val authToken = "Bearer $token"

                // 1. Request a pre-signed upload URL
                val uploadDetails = RetrofitInstance.api.createUploadUrl(
                    token = authToken,
                    request = CreateUploadUrlRequest(title)
                )

                // 2. Prepare the PDF
                val preparedUpload = withContext(Dispatchers.IO) {
                    createPdfUploadFile(context, uri)
                }

                tempFile = preparedUpload.tempFile

                // 3. Upload PDF directly to S3
                val requestBody = preparedUpload.tempFile.asRequestBody(
                    "application/pdf".toMediaType()
                )

                val s3Response = RetrofitInstance.api.uploadPdfToS3(
                    uploadUrl = uploadDetails.uploadUrl,
                    contentType = "application/pdf",
                    file = requestBody
                )

                if (!s3Response.isSuccessful) {
                    throw IOException("S3 upload failed: HTTP ${s3Response.code()}")
                }

                // 4. Confirm upload with our backend
                val newBook = RetrofitInstance.api.confirmUpload(
                    token = authToken,
                    request = ConfirmUploadRequest(
                        bookId = uploadDetails.bookId,
                        title = title,
                        fileKey = uploadDetails.fileKey
                    )
                )

                // 5. Update UI
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    books = _uiState.value.books + newBook,
                    errorMessage = null,
                    uploadSuccess = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to upload book",
                    uploadSuccess = false
                )
            } finally {
                // Clean up the temporary file
                tempFile?.delete()
            }
        }
    }

}