package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun LeaderboardsScreen(
    playerStats: List<PlayerStats>,
    onBack: () -> Unit,
    onSelectPlayer: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("BATTING") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboards", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp)
                .testTag("leaderboards_screen")
        ) {
            // Category Tabs
            TabRow(
                selectedTabIndex = if (selectedCategory == "BATTING") 0 else 1,
                containerColor = HighDensityChipBg,
                contentColor = HighDensityGreenHeader
            ) {
                Tab(
                    selected = (selectedCategory == "BATTING"),
                    onClick = { selectedCategory = "BATTING" },
                    modifier = Modifier.testTag("tab_batting")
                ) {
                    Text("🏏 BATTING", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(
                    selected = (selectedCategory == "BOWLING"),
                    onClick = { selectedCategory = "BOWLING" },
                    modifier = Modifier.testTag("tab_bowling")
                ) {
                    Text("🎯 BOWLING", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val sortedList = if (selectedCategory == "BATTING") {
                playerStats.sortedByDescending { it.runsScored }
            } else {
                playerStats.sortedByDescending { it.wicketsTaken }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(sortedList) { index, stats ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, HighDensityBorder),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().testTag("leaderboard_row_$index")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when (index) {
                                        0 -> "🥇"
                                        1 -> "🥈"
                                        2 -> "🥉"
                                        else -> "${index + 1}."
                                    },
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = stats.playerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
                                    Text(text = stats.teamName, fontSize = 11.sp, color = HighDensityTextSecondary)
                                }
                            }

                            if (selectedCategory == "BATTING") {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "${stats.runsScored} Runs", fontSize = 16.sp, fontWeight = FontWeight.Black, color = HighDensityGreenHeader)
                                    Text(text = "4s: ${stats.fours} | 6s: ${stats.sixes}", fontSize = 11.sp, color = HighDensityTextSecondary)
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "${stats.wicketsTaken} Wkts", fontSize = 16.sp, fontWeight = FontWeight.Black, color = HighDensityGreenHeader)
                                    Text(text = "Econ: ${String.format("%.2f", stats.bowlingEconomy)}", fontSize = 11.sp, color = HighDensityTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

