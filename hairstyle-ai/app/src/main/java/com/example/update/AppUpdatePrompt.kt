package com.example.update

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ElectricVioletSoft
import com.example.ui.theme.HairLookCardBg
import com.example.ui.theme.HairLookPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Update UI drawn above the app: background download progress, "restart to update",
 * and the update dialog for sideloaded installs (blocking when the update is required).
 */
@Composable
fun AppUpdatePrompt(controller: AppUpdateController) {
    val state by controller.state.collectAsState()
    when (val current = state) {
        AppUpdateController.UiState.Idle -> Unit
        is AppUpdateController.UiState.Downloading -> UpdateBanner {
            Text(
                text = "Downloading update… ${(current.progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { current.progress },
                modifier = Modifier.fillMaxWidth(),
                color = ElectricVioletSoft
            )
        }
        AppUpdateController.UiState.ReadyToInstall -> UpdateBanner {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = ElectricVioletSoft)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Update downloaded",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { controller.completeFlexibleUpdate() },
                    colors = ButtonDefaults.buttonColors(containerColor = HairLookPurple),
                    modifier = Modifier.testTag("update_restart_btn")
                ) {
                    Text("Restart")
                }
            }
        }
        is AppUpdateController.UiState.SelfHostedAvailable -> {
            val manifest = current.manifest
            AlertDialog(
                onDismissRequest = {
                    if (!current.isRequired) controller.dismissSelfHostedUpdate(manifest)
                },
                properties = DialogProperties(
                    dismissOnBackPress = !current.isRequired,
                    dismissOnClickOutside = !current.isRequired
                ),
                icon = { Icon(Icons.Default.SystemUpdate, contentDescription = null) },
                title = { Text(if (current.isRequired) "Update required" else "Update available") },
                text = {
                    Column {
                        Text(
                            text = "HairLook ${manifest.versionName} is available.",
                            fontWeight = FontWeight.SemiBold
                        )
                        if (manifest.releaseNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(manifest.releaseNotes, color = TextSecondary)
                        }
                        if (current.isRequired) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "This version is no longer supported. Please update to keep using HairLook.",
                                color = TextSecondary
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { controller.downloadSelfHostedUpdate(manifest) },
                        colors = ButtonDefaults.buttonColors(containerColor = HairLookPurple),
                        modifier = Modifier.testTag("update_download_btn")
                    ) {
                        Text("Download Update")
                    }
                },
                dismissButton = if (current.isRequired) null else {
                    {
                        TextButton(onClick = { controller.dismissSelfHostedUpdate(manifest) }) {
                            Text("Later")
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun UpdateBanner(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 88.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HairLookCardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                content()
            }
        }
    }
}
