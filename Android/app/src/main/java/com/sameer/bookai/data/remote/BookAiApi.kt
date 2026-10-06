package com.sameer.bookai.data.remote

import com.sameer.bookai.data.remote.model.AuthSessionResponse
import com.sameer.bookai.data.remote.model.Book
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
}