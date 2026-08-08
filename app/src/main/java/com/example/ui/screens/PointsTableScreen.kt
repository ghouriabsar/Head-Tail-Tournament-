package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TeamPointsRow
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsTableScreen(
    pointsTable: List<TeamPointsRow>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Points Table", fontWeight = FontWeight.Bold) },
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
                .testTag("points_table_screen"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Final Qualified Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HighDensitySurfaceVariant),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, HighDensityBorderDark)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = HighDensityGreenHeader, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "QUALIFICATION RULES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HighDensityGreenHeader)
                            Text(text = "Top 2 teams automatically qualify for the Final!", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = HighDensityTextPrimary)
                        }
                    }
                }
            }

            // Table Headers
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HighDensityGreenHeader),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "POS & TEAM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(2.5f))
                        Text(text = "P", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.7f))
                        Text(text = "W", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.7f))
                        Text(text = "D", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.7f))
                        Text(text = "L", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.7f))
                        Text(text = "NRR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1.2f))
                        Text(text = "PTS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.weight(0.9f))
                    }
                }
            }

            // Standings Rows
            items(pointsTable) { row ->
                val isTopTwo = row.position <= 2
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isTopTwo) MaterialTheme.colorScheme.surface else HighDensityChipBg
                    ),
                    border = BorderStroke(1.dp, if (isTopTwo) HighDensityBorder else HighDensityBorderDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("points_row_${row.position}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Position & Medal
                        Row(modifier = Modifier.weight(2.5f), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (row.position) {
                                    1 -> "🥇"
                                    2 -> "🥈"
                                    3 -> "🥉"
                                    else -> "${row.position}."
                                },
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = row.teamName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HighDensityTextPrimary
                                )
                                if (isTopTwo) {
                                    Text(text = "Final Qualifier", fontSize = 9.sp, color = HighDensityGreenHeader, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Text(text = "${row.played}", fontSize = 12.sp, color = HighDensityTextPrimary, modifier = Modifier.weight(0.7f))
                        Text(text = "${row.won}", fontSize = 12.sp, color = HighDensityTextPrimary, modifier = Modifier.weight(0.7f))
                        Text(text = "${row.draw}", fontSize = 12.sp, color = HighDensityTextPrimary, modifier = Modifier.weight(0.7f))
                        Text(text = "${row.lost}", fontSize = 12.sp, color = HighDensityTextPrimary, modifier = Modifier.weight(0.7f))
                        Text(
                            text = String.format("%+.2f", row.netRunRate),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (row.netRunRate >= 0) HighDensityGreenHeader else BoundaryRed,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(text = "${row.points}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = HighDensityGreenHeader, modifier = Modifier.weight(0.9f))
                    }
                }
            }
        }
    }
}

