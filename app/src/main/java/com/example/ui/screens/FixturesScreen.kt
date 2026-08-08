package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.MatchEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixturesScreen(
    matches: List<MatchEntity>,
    onBack: () -> Unit,
    onOpenMatch: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fixtures", fontWeight = FontWeight.Bold) },
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
                .testTag("fixtures_screen"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(matches) { match ->
                FixtureMatchCard(
                    match = match,
                    onClick = { onOpenMatch(match.id) }
                )
            }
        }
    }
}

@Composable
fun FixtureMatchCard(
    match: MatchEntity,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (match.isFinal) HighDensitySurfaceVariant else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (match.isFinal) HighDensityBorderDark else HighDensityBorder),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fixture_card_${match.matchNumber}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (match.isFinal) "🏆 FINAL MATCH" else "MATCH #${match.matchNumber}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighDensityGreenHeader,
                    letterSpacing = 0.5.sp
                )

                Surface(
                    color = when (match.status) {
                        "COMPLETED" -> HighDensityChipBg
                        "LIVE" -> HighDensityGreenHeader
                        else -> HighDensityChipBg
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = match.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (match.status == "LIVE") Color.White else HighDensityTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team A
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = match.teamA, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
                    if (match.status != "SCHEDULED") {
                        Text(
                            text = "${match.teamAScore}/${match.teamAWickets} (${match.teamABalls / 6}.${match.teamABalls % 6} ov)",
                            fontSize = 12.sp,
                            color = HighDensityTextSecondary
                        )
                    }
                }

                Surface(
                    color = HighDensityChipBg,
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "VS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = HighDensityTextSecondary)
                    }
                }

                // Team B
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = match.teamB, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
                    if (match.status != "SCHEDULED") {
                        Text(
                            text = "${match.teamBScore}/${match.teamBWickets} (${match.teamBBalls / 6}.${match.teamBBalls % 6} ov)",
                            fontSize = 12.sp,
                            color = HighDensityTextSecondary
                        )
                    }
                }
            }

            if (match.status == "COMPLETED" && match.winningMargin != null) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = HighDensityBorder)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Result: ${match.winningMargin}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighDensityGreenHeader
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (match.status == "COMPLETED") HighDensityChipBg else HighDensityGreenHeader,
                    contentColor = if (match.status == "COMPLETED") HighDensityTextPrimary else Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("fixture_action_button_${match.matchNumber}")
            ) {
                Icon(
                    imageVector = if (match.status == "COMPLETED") Icons.Default.Visibility else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (match.status) {
                        "COMPLETED" -> "View Scorecard"
                        "LIVE" -> "Continue Scoring"
                        else -> "Start Match"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

