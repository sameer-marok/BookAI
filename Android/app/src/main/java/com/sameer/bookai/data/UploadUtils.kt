package com.sameer.bookai.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

// Utility function to create a MultipartBody.Part from a PDF file Uri
fun createPdfPart(
    context: Context,
    uri: Uri
): PreparedPdfUpload {
    val fileName = context.contentResolver.query(
        uri, null, null, null, null
    )?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) {
            cursor.getString(nameIndex)
        } else null
    } ?: "book.pdf"

    val tempFile = File.createTempFile("book_upload_", ".pdf", context.cacheDir)

    try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Unable to open selected PDF")

        val requestBody = tempFile.asRequestBody("application/pdf".toMediaType())

        return PreparedPdfUpload(
            filePart = MultipartBody.Part.createFormData(
                "file", // The name of the part in the multipart request
                fileName, // The original file name
                requestBody // The request body containing the file data
            ),
            tempFile = tempFile
        )

    } catch (e: Exception) {
        tempFile.delete()
        throw e
    }
}

// Data class to hold the prepared PDF upload information
data class PreparedPdfUpload(
    val filePart: MultipartBody.Part,
    val tempFile: File
)
