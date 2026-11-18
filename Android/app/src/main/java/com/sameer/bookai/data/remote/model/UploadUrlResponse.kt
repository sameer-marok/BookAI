package com.sameer.bookai.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadUrlResponse(
    val bookId: String,
    val fileKey: String,
    val uploadUrl: String
)
