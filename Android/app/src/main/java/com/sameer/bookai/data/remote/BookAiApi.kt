package com.sameer.bookai.data.remote

import com.sameer.bookai.data.remote.model.AuthSessionResponse
import retrofit2.http.Header
import retrofit2.http.POST

interface BookAiApi {

    @POST("/api/auth/session")
    suspend fun createSession(
        @Header("Authorization") token: String
    ): AuthSessionResponse
}