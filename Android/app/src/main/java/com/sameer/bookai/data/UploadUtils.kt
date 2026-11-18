package com.sameer.bookai.data

import android.content.Context
import android.net.Uri
import java.io.File

data class PreparedPdfUpload(
    val tempFile: File
)

// Utility function to create a temporary PDF file from a Uri
fun createPdfUploadFile(context: Context, uri: Uri): PreparedPdfUpload {
    val tempFile = File.createTempFile(
        "book_upload_",
        ".pdf",
        context.cacheDir
    )

    try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Unable to open selected PDF")

        return PreparedPdfUpload(tempFile)
    } catch (e: Exception) {
        tempFile.delete()
        throw e
    }
}