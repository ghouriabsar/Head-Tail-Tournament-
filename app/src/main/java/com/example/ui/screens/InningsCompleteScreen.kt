package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun InningsCompleteScreen(
    battingTeam: String,
    totalRuns: Int,
    wickets: Int,
    overs: String,
    targetScore: Int,
    onStartSecondInnings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HighDensityBg)
            .padding(20.dp)
            .testTag("innings_complete_screen"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, HighDensityBorder),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.SportsCricket,
                    contentDescription = null,
                    tint = HighDensityGreenHeader,
                    modifier = Modifier.size(52.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "INNINGS COMPLETE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = HighDensityGreenHeader,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = battingTeam,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighDensityTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$totalRuns/$wickets in $overs ov",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = HighDensityGreenHeader
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    color = HighDensityGreenHeader,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "TARGET FOR 2ND INNINGS", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), letterSpacing = 1.sp)
                        Text(text = "$targetScore RUNS", fontSize = 26.sp, fontWeight = FontWeight.Black, color = GoldAccent)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onStartSecondInnings,
                    colors = ButtonDefaults.buttonColors(containerColor = HighDensityGreenHeader),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_2nd_innings_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("START 2ND INNINGS", fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun SuperOverScreen(
    teamA: String,
    teamB: String,
    onSelectWinner: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HighDensityBg)
            .padding(20.dp)
            .testTag("super_over_screen"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = HighDensityGreenHeader),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚡ SUPER OVER RESULT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldAccent
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Who won the Super Over?",
                    fontSize = 15.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { onSelectWinner(teamA) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("super_over_winner_team_a")
                ) {
                    Text(text = "$teamA Won Super Over", color = Color.Black, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { onSelectWinner(teamB) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("super_over_winner_team_b")
                ) {
                    Text(text = "$teamB Won Super Over", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

