package com.noter.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.core.v2.files.FileMetadata
import com.noter.auth.AuthResult
import com.noter.repository.DropboxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class HomeUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val pdfs: List<FileMetadata> = emptyList(),
    val oauthUrl: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dropboxRepository: DropboxRepository
) : ViewModel() {
    val uiState = mutableStateOf(HomeUiState())

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val isAuth = dropboxRepository.isAuthenticated()
        uiState.value = uiState.value.copy(isAuthenticated = isAuth)
        if (isAuth) {
            loadPdfs()
        }
    }

    fun initiateDropboxLogin() {
        val oauthUrl = dropboxRepository.getOAuthStartUrl()
        uiState.value = uiState.value.copy(oauthUrl = oauthUrl)
        Timber.d("OAuth URL generated: $oauthUrl")
    }

    fun handleOAuthCallback(code: String?, state: String?) {
        if (code == null || state == null) {
            uiState.value = uiState.value.copy(error = "OAuth callback missing parameters")
            return
        }

        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true, error = null)
            try {
                val result = dropboxRepository.handleOAuthCallback(code, state)
                when (result) {
                    is AuthResult.Success -> {
                        uiState.value = uiState.value.copy(
                            isAuthenticated = true,
                            isLoading = false,
                            oauthUrl = null
                        )
                        loadPdfs()
                    }
                    is AuthResult.Error -> {
                        uiState.value = uiState.value.copy(
                            error = result.exception.message ?: "OAuth failed",
                            isLoading = false
                        )
                        Timber.e(result.exception, "OAuth error")
                    }
                    is AuthResult.Loading -> {
                        uiState.value = uiState.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling OAuth callback")
                uiState.value = uiState.value.copy(
                    error = "Failed to complete authentication",
                    isLoading = false
                )
            }
        }
    }

    fun loadPdfs() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true, error = null)
            try {
                val result = dropboxRepository.listPdfs()
                when (result) {
                    is AuthResult.Success -> {
                        uiState.value = uiState.value.copy(
                            pdfs = result.data,
                            isLoading = false
                        )
                        Timber.d("Loaded ${result.data.size} PDFs")
                    }
                    is AuthResult.Error -> {
                        uiState.value = uiState.value.copy(
                            error = result.exception.message ?: "Failed to load PDFs",
                            isLoading = false
                        )
                    }
                    is AuthResult.Loading -> {
                        uiState.value = uiState.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading PDFs")
                uiState.value = uiState.value.copy(
                    error = "Failed to load PDFs",
                    isLoading = false
                )
            }
        }
    }

    fun signOut() {
        dropboxRepository.clearAuth()
        uiState.value = HomeUiState()
        Timber.d("User signed out")
    }
}
