package com.sameer.bookai.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateUploadUrlRequest(
    val title: String
)
