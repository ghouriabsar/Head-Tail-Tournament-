package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.BallEntity
import com.example.data.db.MatchEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    match: MatchEntity?,
    balls: List<BallEntity>,
    onBack: () -> Unit,
    onDeleteBall: (Long) -> Unit
) {
    if (match == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match #${match.matchNumber} Scorecard", fontWeight = FontWeight.Bold) },
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
                .testTag("match_detail_screen"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Summary Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HighDensityGreenHeader),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "${match.teamA} vs ${match.teamB}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Toss Winner: ${match.tossWinner ?: "N/A"} (${match.tossDecision ?: ""})", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Result: ${match.winningMargin ?: "In Progress"}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    }
                }
            }

            // Ball by ball log
            item {
                Text(text = "Delivery History", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
            }

            items(balls.reversed()) { ball ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("ball_log_row_${ball.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Inn ${ball.innings} • Ov ${ball.overNumber}.${ball.ballNumber}: ${ball.batsmanName} vs ${ball.bowlerName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HighDensityTextPrimary
                            )
                            Text(
                                text = "Runs: ${ball.runs} ${if (ball.isWicket) "| WICKET (${ball.dismissalType})" else ""}",
                                fontSize = 11.sp,
                                color = if (ball.isWicket) BoundaryRed else HighDensityGreenHeader
                            )
                        }

                        IconButton(
                            onClick = { onDeleteBall(ball.id) },
                            modifier = Modifier.testTag("delete_ball_button_${ball.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Ball", tint = BoundaryRed)
                        }
                    }
                }
            }
        }
    }
}

