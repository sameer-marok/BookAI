package com.sameer.bookai.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateBookRequest(
    val title: String
)