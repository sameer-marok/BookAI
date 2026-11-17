package com.sameer.bookai.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmUploadRequest(
    val bookId: String,
    val title: String,
    val fileKey: String
)
