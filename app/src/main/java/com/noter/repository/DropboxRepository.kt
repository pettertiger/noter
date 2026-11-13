package com.noter.repository

import android.content.Context
import android.net.Uri
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.noter.auth.AuthResult
import com.noter.auth.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.UUID

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String?,
    val expiresAt: Long
)

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

    fun getOAuthStartUrl(): String {
        val state = UUID.randomUUID().toString()
        tokenManager.saveOAuthState(state)
        
        return "https://www.dropbox.com/oauth2/authorize?" +
            "client_id=$APP_KEY" +
            "&response_type=code" +
            "&redirect_uri=${Uri.encode(REDIRECT_URI)}" +
            "&state=$state" +
            "&token_access_type=offline"
    }

    suspend fun handleOAuthCallback(code: String, state: String): AuthResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val savedState = tokenManager.getOAuthState()
                if (savedState != state) {
                    return@withContext AuthResult.Error(
                        Exception("OAuth state mismatch - possible CSRF attack")
                    )
                }

                val tokenResponse = exchangeCodeForToken(code)
                if (tokenResponse != null) {
                    tokenManager.saveToken(
                        provider = PROVIDER_NAME,
                        accessToken = tokenResponse.accessToken,
                        refreshToken = tokenResponse.refreshToken,
                        expiresAt = tokenResponse.expiresAt
                    )
                    initializeClient()
                    Timber.d("OAuth successful - token saved")
                    AuthResult.Success(true)
                } else {
                    AuthResult.Error(Exception("Failed to exchange code for token"))
                }
            } catch (e: Exception) {
                Timber.e(e, "OAuth callback error")
                AuthResult.Error(e)
            }
        }
    }

    private suspend fun exchangeCodeForToken(code: String): TokenResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://api.dropbox.com/oauth2/token"
                val params = "code=$code" +
                    "&grant_type=authorization_code" +
                    "&client_id=$APP_KEY" +
                    "&client_secret=$APP_SECRET" +
                    "&redirect_uri=${Uri.encode(REDIRECT_URI)}"

                val conn = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.doOutput = true

                conn.outputStream.use { os ->
                    os.write(params.toByteArray(Charsets.UTF_8))
                }

                if (conn.responseCode == 200) {
                    val response = conn.inputStream.bufferedReader().use { it.readText() }
                    parseTokenResponse(response)
                } else {
                    val error = conn.errorStream?.bufferedReader()?.use { it.readText() }
                    Timber.e("Token exchange failed: $error")
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Error exchanging code for token")
                null
            }
        }
    }

    private fun parseTokenResponse(json: String): TokenResponse? {
        return try {
            val accessToken = extractJsonValue(json, "access_token") ?: return null
            val expiresIn = extractJsonValue(json, "expires_in")?.toLongOrNull() ?: 3600L
            val refreshToken = extractJsonValue(json, "refresh_token")

            TokenResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresAt = System.currentTimeMillis() + (expiresIn * 1000)
            )
        } catch (e: Exception) {
            Timber.e(e, "Error parsing token response")
            null
        }
    }

    private fun extractJsonValue(json: String, key: String): String? {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        return pattern.find(json)?.groupValues?.get(1)
    }

    suspend fun listPdfs(folderPath: String = ""): AuthResult<List<FileMetadata>> {
        return withContext(Dispatchers.IO) {
            try {
                if (dbxClient == null) {
                    return@withContext AuthResult.Error(
                        Exception("Not authenticated with Dropbox")
                    )
                }

                var listResult = dbxClient!!.files().listFolder(folderPath)
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
        // These values will be set from Dropbox Developer Console
        const val APP_KEY = BuildConfig.DROPBOX_APP_KEY
        const val APP_SECRET = BuildConfig.DROPBOX_APP_SECRET
        const val REDIRECT_URI = "noter://oauth-callback"
    }
}
