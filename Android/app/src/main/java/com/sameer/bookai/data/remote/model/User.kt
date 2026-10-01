package com.sameer.bookai.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val firebaseUid: String,
    val email: String? = null
)
