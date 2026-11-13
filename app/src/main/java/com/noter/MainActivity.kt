package com.noter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.noter.ui.screens.AuthenticationScreen
import com.noter.ui.screens.HomeScreen
import com.noter.ui.theme.NoterTheme
import com.noter.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NoterApp()
                }
            }
        }
    }
}

@Composable
fun NoterApp(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState

    if (uiState.isAuthenticated) {
        HomeScreen(
            uiState = uiState,
            onLoadPdfs = { homeViewModel.loadPdfs() },
            onSignOut = { homeViewModel.signOut() },
            onSelectPdf = { filePath ->
                Timber.d("Selected PDF: $filePath")
            }
        )
    } else {
        AuthenticationScreen(
            onConnectDropbox = {
                Timber.d("Connect Dropbox clicked")
                // TODO: Implement OAuth flow
            },
            onConnectGoogleDrive = {
                Timber.d("Connect Google Drive clicked")
                // TODO: Implement OAuth flow
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoterAppPreview() {
    NoterTheme {
        AuthenticationScreen(
            onConnectDropbox = {},
            onConnectGoogleDrive = {}
        )
    }
}
