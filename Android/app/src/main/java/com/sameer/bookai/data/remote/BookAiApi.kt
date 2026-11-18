package com.sameer.bookai.data.remote

import com.sameer.bookai.data.remote.model.AuthSessionResponse
import com.sameer.bookai.data.remote.model.Book
import com.sameer.bookai.data.remote.model.ConfirmUploadRequest
import com.sameer.bookai.data.remote.model.CreateUploadUrlRequest
import com.sameer.bookai.data.remote.model.UploadUrlResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface BookAiApi {

    @POST("/api/auth/session")
    suspend fun createSession(
        @Header("Authorization") token: String
    ): AuthSessionResponse

    @GET("/api/books")
    suspend fun getBooks(
        @Header("Authorization") token: String
    ): List<Book>

    @POST("api/books/upload-url")
    suspend fun createUploadUrl(
        @Header("Authorization") token: String,
        @Body request: CreateUploadUrlRequest
    ): UploadUrlResponse

    @POST("api/books/confirm-upload")
    suspend fun confirmUpload(
        @Header("Authorization") token: String,
        @Body request: ConfirmUploadRequest
    ): Book

    // Endpoint to upload a PDF file to S3 using the pre-signed URL
    @PUT
    suspend fun uploadPdfToS3(
        @Url uploadUrl: String,
        // The content type of the file being uploaded (e.g., "application/pdf")
        @Header("Content-Type") contentType: String,
        @Body file: RequestBody // The request body containing the file data
    ): Response<Unit>
}