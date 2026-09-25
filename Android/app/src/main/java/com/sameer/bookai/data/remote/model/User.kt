package com.sameer.bookai.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String,
    val email: String? = null
)
