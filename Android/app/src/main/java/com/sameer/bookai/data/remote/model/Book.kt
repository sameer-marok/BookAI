package com.sameer.bookai.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val title: String
)
