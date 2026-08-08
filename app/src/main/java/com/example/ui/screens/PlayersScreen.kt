package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayerStats
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    playerStats: List<PlayerStats>,
    onBack: () -> Unit,
    onSelectPlayer: (String) -> Unit,
    onAddPlayer: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredList = playerStats.filter {
        it.playerName.contains(searchQuery, ignoreCase = true) ||
                it.teamName.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Players", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_player_fab_icon")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Player", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HighDensityGreenHeader, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = HighDensityBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp)
                .testTag("players_screen")
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search player or team...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = HighDensityGreenHeader) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HighDensityGreenHeader,
                    unfocusedBorderColor = HighDensityBorder,
                    focusedTextColor = HighDensityTextPrimary,
                    unfocusedTextColor = HighDensityTextPrimary,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { stats ->
                    PlayerCard(
                        stats = stats,
                        onClick = { onSelectPlayer(stats.playerName) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newTeam by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Player") },
            text = {
                Column {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Player Name") }, modifier = Modifier.fillMaxWidth().testTag("add_player_name"))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newTeam, onValueChange = { newTeam = it }, label = { Text("Team Name") }, modifier = Modifier.fillMaxWidth().testTag("add_player_team"))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onAddPlayer(newName, newTeam)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HighDensityGreenHeader),
                    modifier = Modifier.testTag("save_add_player_button")
                ) {
                    Text("Save Player")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun PlayerCard(
    stats: PlayerStats,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, HighDensityBorder),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("player_card_${stats.playerName}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stats.playerName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
                Text(text = stats.teamName.ifBlank { "Free Agent" }, fontSize = 12.sp, color = HighDensityTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Matches: ${stats.matchesPlayed}", fontSize = 11.sp, color = HighDensityTextPrimary)
                    Text(text = "Runs: ${stats.runsScored}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HighDensityGreenHeader)
                    Text(text = "Wkts: ${stats.wicketsTaken}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HighDensitySecondaryPill)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Avg: ${String.format("%.1f", stats.battingAverage)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
                Text(text = "SR: ${String.format("%.1f", stats.strikeRate)}", fontSize = 11.sp, color = HighDensityTextSecondary)
                Text(text = "HS: ${stats.highestScore}", fontSize = 11.sp, color = HighDensityGreenHeader, fontWeight = FontWeight.Bold)
            }
        }
    }
}

