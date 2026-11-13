package com.noter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@Composable
fun NoterApp(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState
    val context = LocalContext.current
    
    val intent = remember { (context as? ComponentActivity)?.intent }
    val uri = remember { intent?.data }

    // Handle OAuth callback if app was opened via deep link
    if (uri != null && uri.scheme == "noter" && uri.host == "oauth-callback") {
        val code = uri.getQueryParameter("code")
        val state = uri.getQueryParameter("state")
        if (code != null && state != null) {
            Timber.d("Handling OAuth callback: code=$code, state=$state")
            homeViewModel.handleOAuthCallback(code, state)
        }
    }

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
                Timber.d("Initiating Dropbox login")
                homeViewModel.initiateDropboxLogin()
                val oauthUrl = homeViewModel.uiState.value.oauthUrl
                if (!oauthUrl.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(oauthUrl))
                    context.startActivity(intent)
                }
            },
            onConnectGoogleDrive = {
                Timber.d("Google Drive not yet implemented")
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
