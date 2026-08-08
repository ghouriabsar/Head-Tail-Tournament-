package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun PlayerDetailScreen(
    stats: PlayerStats?,
    onBack: () -> Unit
) {
    if (stats == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${stats.playerName}'s Profile", fontWeight = FontWeight.Bold) },
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
                .testTag("player_detail_screen"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Header Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HighDensityGreenHeader),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = GoldAccent,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = stats.playerName, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text(text = stats.teamName.ifBlank { "All-Time Record" }, fontSize = 12.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Batting Stats Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "🏏 Batting Career Statistics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HighDensityGreenHeader)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StatTile("Matches", "${stats.matchesPlayed}")
                            StatTile("Innings", "${stats.inningsBatted}")
                            StatTile("Runs", "${stats.runsScored}", highlight = true)
                            StatTile("HS", "${stats.highestScore}")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StatTile("Average", String.format("%.2f", stats.battingAverage))
                            StatTile("Strike Rate", String.format("%.2f", stats.strikeRate))
                            StatTile("4s", "${stats.fours}")
                            StatTile("6s", "${stats.sixes}")
                        }
                    }
                }
            }

            // Bowling Stats Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "🎯 Bowling Career Statistics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HighDensityGreenHeader)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StatTile("Wickets", "${stats.wicketsTaken}", highlight = true)
                            StatTile("Best", stats.bestBowlingString)
                            StatTile("Bowled", "${stats.ballsBowled / 6}.${stats.ballsBowled % 6} ov")
                            StatTile("Economy", String.format("%.2f", stats.bowlingEconomy))
                        }
                    }
                }
            }

            // Tournament History Breakdown
            item {
                Text(text = "🏆 Tournament History", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
            }

            if (stats.tournamentHistory.isEmpty()) {
                item {
                    Text(text = "No previous tournament records yet.", fontSize = 12.sp, color = HighDensityTextSecondary)
                }
            } else {
                items(stats.tournamentHistory) { history ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, HighDensityBorder),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = history.tournamentName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
                                Text(text = "${history.matches} Matches", fontSize = 11.sp, color = HighDensityTextSecondary)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "${history.runs} Runs", fontSize = 13.sp, fontWeight = FontWeight.Black, color = HighDensityGreenHeader)
                                    Text(text = "HS: ${history.highestScore}", fontSize = 10.sp, color = HighDensityTextSecondary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "${history.wickets} Wkts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HighDensityGreenHeader)
                                    Text(text = "Avg: ${String.format("%.1f", history.average)}", fontSize = 10.sp, color = HighDensityTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = if (highlight) HighDensityGreenHeader else HighDensityTextPrimary
        )
        Text(text = label, fontSize = 10.sp, color = HighDensityTextSecondary)
    }
}

