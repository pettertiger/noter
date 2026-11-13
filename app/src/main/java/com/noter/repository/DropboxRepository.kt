package com.noter.repository

import android.content.Context
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.noter.auth.AuthResult
import com.noter.auth.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

/**
 * Repository for interacting with Dropbox API.
 * Handles OAuth flow, file listing, and PDF download.
 */
class DropboxRepository(
    private val context: Context,
    private val tokenManager: TokenManager
) {
    private var dbxClient: DbxClientV2? = null

    init {
        initializeClient()
    }

    private fun initializeClient() {
        val accessToken = tokenManager.getAccessToken(PROVIDER_NAME)
        if (accessToken != null && !tokenManager.isTokenExpired(PROVIDER_NAME)) {
            val config = DbxRequestConfig.newBuilder("Noter/1.0").build()
            dbxClient = DbxClientV2(config, accessToken)
            Timber.d("Dropbox client initialized with existing token")
        }
    }

    suspend fun listPdfs(folderPath: String = ""): AuthResult<List<FileMetadata>> {
        return withContext(Dispatchers.IO) {
            try {
                if (dbxClient == null) {
                    return@withContext AuthResult.Error(
                        Exception("Not authenticated with Dropbox")
                    )
                }

                val listResult = dbxClient!!.files().listFolder(folderPath)
                val pdfFiles = mutableListOf<FileMetadata>()

                while (true) {
                    for (entry in listResult.entries) {
                        if (entry is FileMetadata && entry.name.endsWith(".pdf")) {
                            pdfFiles.add(entry)
                        }
                    }
                    if (!listResult.hasMore) break
                    listResult = dbxClient!!.files().listFolderContinue(listResult.cursor)
                }

                Timber.d("Found ${pdfFiles.size} PDFs in Dropbox")
                AuthResult.Success(pdfFiles)
            } catch (e: DbxException) {
                Timber.e(e, "Error listing PDFs from Dropbox")
                AuthResult.Error(e)
            }
        }
    }

    suspend fun downloadPdf(filePath: String, outputFile: File): AuthResult<File> {
        return withContext(Dispatchers.IO) {
            try {
                if (dbxClient == null) {
                    return@withContext AuthResult.Error(
                        Exception("Not authenticated with Dropbox")
                    )
                }

                outputFile.parentFile?.mkdirs()
                dbxClient!!.files().downloadBuilder(filePath)
                    .download(outputFile.outputStream())

                Timber.d("Downloaded PDF: $filePath")
                AuthResult.Success(outputFile)
            } catch (e: DbxException) {
                Timber.e(e, "Error downloading PDF from Dropbox: $filePath")
                AuthResult.Error(e)
            }
        }
    }

    suspend fun uploadAnnotations(
        filePath: String,
        annotationsJson: String
    ): AuthResult<FileMetadata> {
        return withContext(Dispatchers.IO) {
            try {
                if (dbxClient == null) {
                    return@withContext AuthResult.Error(
                        Exception("Not authenticated with Dropbox")
                    )
                }

                val annotationPath = "$filePath.noter.json"
                val result = dbxClient!!.files().uploadBuilder(annotationPath)
                    .withAutorename(false)
                    .uploadAndFinish(annotationsJson.byteInputStream())

                Timber.d("Uploaded annotations: $annotationPath")
                AuthResult.Success(result)
            } catch (e: DbxException) {
                Timber.e(e, "Error uploading annotations to Dropbox")
                AuthResult.Error(e)
            }
        }
    }

    fun isAuthenticated(): Boolean {
        return dbxClient != null && tokenManager.hasToken(PROVIDER_NAME)
    }

    fun clearAuth() {
        dbxClient = null
        tokenManager.clearToken(PROVIDER_NAME)
        Timber.d("Dropbox authentication cleared")
    }

    companion object {
        private const val PROVIDER_NAME = "dropbox"
    }
}
