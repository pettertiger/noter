package com.noter.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import timber.log.Timber

/**
 * Manages OAuth tokens securely using Android Keystore.
 * Tokens are encrypted at rest and only decrypted when needed.
 */
class TokenManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(
        provider: String,
        accessToken: String,
        refreshToken: String?,
        expiresAt: Long
    ) {
        encryptedPrefs.edit().apply {
            putString("${provider}_access_token", accessToken)
            refreshToken?.let { putString("${provider}_refresh_token", it) }
            putLong("${provider}_expires_at", expiresAt)
            apply()
        }
        Timber.d("Token saved for provider: $provider")
    }

    fun getAccessToken(provider: String): String? {
        return encryptedPrefs.getString("${provider}_access_token", null)
    }

    fun getRefreshToken(provider: String): String? {
        return encryptedPrefs.getString("${provider}_refresh_token", null)
    }

    fun getExpiresAt(provider: String): Long {
        return encryptedPrefs.getLong("${provider}_expires_at", 0)
    }

    fun isTokenExpired(provider: String): Boolean {
        val expiresAt = getExpiresAt(provider)
        return System.currentTimeMillis() > expiresAt
    }

    fun clearToken(provider: String) {
        encryptedPrefs.edit().apply {
            remove("${provider}_access_token")
            remove("${provider}_refresh_token")
            remove("${provider}_expires_at")
            apply()
        }
        Timber.d("Token cleared for provider: $provider")
    }

    fun hasToken(provider: String): Boolean {
        return getAccessToken(provider) != null
    }

    fun saveOAuthState(state: String) {
        encryptedPrefs.edit().apply {
            putString("oauth_state", state)
            apply()
        }
    }

    fun getOAuthState(): String? {
        return encryptedPrefs.getString("oauth_state", null)
    }

    fun clearOAuthState() {
        encryptedPrefs.edit().apply {
            remove("oauth_state")
            apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "noter_encrypted_prefs"
    }
}
