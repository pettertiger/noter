package com.noter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noter.viewmodel.HomeUiState

@Composable
fun AuthenticationScreen(
    onConnectDropbox: () -> Unit,
    onConnectGoogleDrive: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Text(
                "Connecting to Dropbox...",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            Text(
                text = "Welcome to Noter",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Text(
                text = "Connect your cloud storage to read and annotate your music notes",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = onConnectDropbox,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = !isLoading
            ) {
                Text("Connect Dropbox")
            }

            Button(
                onClick = onConnectGoogleDrive,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = !isLoading
            ) {
                Text("Connect Google Drive")
            }
        }
    }
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onLoadPdfs: () -> Unit,
    onSignOut: () -> Unit,
    onSelectPdf: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text("Loading...", modifier = Modifier.padding(top = 16.dp))
            }
        }
        uiState.error != null -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Error: ${uiState.error}", style = MaterialTheme.typography.bodyLarge)
                Button(
                    onClick = onLoadPdfs,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Retry")
                }
            }
        }
        uiState.pdfs.isEmpty() -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No PDFs found", style = MaterialTheme.typography.titleLarge)
                Button(
                    onClick = onLoadPdfs,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Reload")
                }
                Button(
                    onClick = onSignOut,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Sign Out")
                }
            }
        }
        else -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Your PDFs (${uiState.pdfs.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // TODO: LazyColumn for PDF list
                Button(
                    onClick = onSignOut,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                ) {
                    Text("Sign Out")
                }
            }
        }
    }
}
