package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onExportBackup: suspend () -> String,
    onResetKeepPlayerStats: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var exportedJson by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Backup", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HighDensityGreenHeader, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = HighDensityBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp)
                .testTag("settings_screen"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Backup Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "💾 Export & Backup Data", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HighDensityGreenHeader)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Backup all tournament matches, ball-by-ball history, and player career records.", fontSize = 12.sp, color = HighDensityTextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    exportedJson = onExportBackup()
                                    Toast.makeText(context, "Backup exported successfully!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HighDensityGreenHeader),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("export_backup_button")
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export JSON Backup", fontWeight = FontWeight.Bold)
                        }

                        if (exportedJson != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = exportedJson ?: "",
                                onValueChange = {},
                                label = { Text("Exported JSON Data") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().height(120.dp)
                            )
                        }
                    }
                }
            }

            // Reset Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "⚠️ Reset Options", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BoundaryRed)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Reset all tournament fixtures while preserving player lifetime career statistics.", fontSize = 12.sp, color = HighDensityTextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BoundaryRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("reset_tournaments_button")
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset Tournaments (Keep Stats)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Tournaments?") },
            text = { Text("This will reset current fixtures and tournament standings. Lifetime player career records will NOT be deleted.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetKeepPlayerStats()
                        showResetDialog = false
                        Toast.makeText(context, "Tournaments reset! Player stats preserved.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BoundaryRed),
                    modifier = Modifier.testTag("confirm_reset_tournaments_btn")
                ) {
                    Text("Confirm Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }
}

