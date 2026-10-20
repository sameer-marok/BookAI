package com.sameer.bookai.data.remote

import com.sameer.bookai.data.remote.model.AuthSessionResponse
import com.sameer.bookai.data.remote.model.Book
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BookAiApi {

    @POST("/api/auth/session")
    suspend fun createSession(
        @Header("Authorization") token: String
    ): AuthSessionResponse

    @GET("/api/books")
    suspend fun getBooks(
        @Header("Authorization") token: String
    ): List<Book>

    // Endpoint to upload a book file
    @Multipart // Indicates that request body is multipart
    @POST("/api/books/upload")
    suspend fun uploadBook(
        @Header("Authorization") token: String,
        // title as a part of the multipart request
        @Part("title") title: RequestBody,
        // book file as a part of the multipart request
        @Part file: MultipartBody.Part
    ): Book
}