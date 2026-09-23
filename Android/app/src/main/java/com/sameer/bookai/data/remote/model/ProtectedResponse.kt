package com.sameer.bookai.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ProtectedResponse(
    val message: String,
    val user: User
)
