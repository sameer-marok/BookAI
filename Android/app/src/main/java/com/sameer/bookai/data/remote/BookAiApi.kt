package com.sameer.bookai.data.remote

import com.sameer.bookai.data.remote.model.ProtectedResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface BookAiApi {
    @GET("api/protected")
    suspend fun getProtected(
        @Header("Authorization") token: String
    ): ProtectedResponse
}