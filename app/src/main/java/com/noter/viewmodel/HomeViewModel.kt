package com.noter.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noter.auth.AuthResult
import com.noter.auth.CloudProvider
import com.noter.repository.DropboxRepository
import com.noter.repository.PdfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dropbox.com.v2.files.FileMetadata
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentProvider: CloudProvider = CloudProvider.NONE,
    val pdfs: List<FileMetadata> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dropboxRepository: DropboxRepository,
    private val pdfRepository: PdfRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(HomeUiState())
    val uiState: State<HomeUiState> = _uiState

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val isDropboxAuth = dropboxRepository.isAuthenticated()
        _uiState.value = _uiState.value.copy(
            isAuthenticated = isDropboxAuth,
            currentProvider = if (isDropboxAuth) CloudProvider.DROPBOX else CloudProvider.NONE
        )
    }

    fun loadPdfs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = dropboxRepository.listPdfs("/")) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        pdfs = result.data,
                        isLoading = false
                    )
                    Timber.d("Loaded ${result.data.size} PDFs")
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.exception.message,
                        isLoading = false
                    )
                    Timber.e(result.exception, "Error loading PDFs")
                }
                is AuthResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun signOut() {
        dropboxRepository.clearAuth()
        _uiState.value = HomeUiState()
        Timber.d("User signed out")
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
