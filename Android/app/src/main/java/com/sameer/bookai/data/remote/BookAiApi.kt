package com.sameer.bookai.data.remote

import com.sameer.bookai.data.remote.model.AuthSessionResponse
import com.sameer.bookai.data.remote.model.Book
import com.sameer.bookai.data.remote.model.CreateBookRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface BookAiApi {

    @POST("/api/auth/session")
    suspend fun createSession(
        @Header("Authorization") token: String
    ): AuthSessionResponse

    @GET("/api/books")
    suspend fun getBooks(
        @Header("Authorization") token: String
    ): List<Book>

    // Endpoint to create a new book
    @POST("/api/books")
    suspend fun createBook(
        @Header("Authorization") token: String,
        @Body request: CreateBookRequest // The request body containing the book title
    ): Book
}