package com.noter.repository

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import timber.log.Timber
import java.io.File

/**
 * Repository for PDF rendering and management.
 * Handles PDF caching and rendering using Android PDFRenderer.
 */
class PdfRepository(private val context: Context) {

    private val cacheDir = File(context.cacheDir, "pdfs")

    init {
        cacheDir.mkdirs()
    }

    fun getPdfRenderer(pdfFile: File): PdfRenderer? {
        return try {
            val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            PdfRenderer(fileDescriptor)
        } catch (e: Exception) {
            Timber.e(e, "Error opening PDF: ${pdfFile.path}")
            null
        }
    }

    fun getCachedPdfs(): List<File> {
        return cacheDir.listFiles { file -> file.extension == "pdf" }?.toList() ?: emptyList()
    }

    fun getCachePath(fileName: String): File {
        return File(cacheDir, fileName)
    }

    fun clearCache() {
        try {
            cacheDir.deleteRecursively()
            cacheDir.mkdirs()
            Timber.d("PDF cache cleared")
        } catch (e: Exception) {
            Timber.e(e, "Error clearing PDF cache")
        }
    }

    data class PdfInfo(
        val file: File,
        val pageCount: Int,
        val sizeBytes: Long
    )

    fun getPdfInfo(pdfFile: File): PdfInfo? {
        return try {
            val renderer = getPdfRenderer(pdfFile) ?: return null
            val pageCount = renderer.pageCount
            renderer.close()

            PdfInfo(
                file = pdfFile,
                pageCount = pageCount,
                sizeBytes = pdfFile.length()
            )
        } catch (e: Exception) {
            Timber.e(e, "Error getting PDF info: ${pdfFile.path}")
            null
        }
    }
}
