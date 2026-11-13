package com.noter.auth

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val exception: Exception) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}

data class AuthToken(
    val accessToken: String,
    val refreshToken: String?,
    val expiresAt: Long,
    val provider: String
)

enum class CloudProvider {
    DROPBOX,
    GOOGLE_DRIVE,
    NONE
}
