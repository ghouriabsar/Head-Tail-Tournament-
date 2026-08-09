package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.MatchEntity
import com.example.data.db.MatchPlayerEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchSetupScreen(
    match: MatchEntity?,
    existingLineup: List<MatchPlayerEntity>,
    onBack: () -> Unit,
    onConfirmSetup: (
        tossWinner: String,
        tossDecision: String,
        teamAPlayers: List<String>,
        teamBPlayers: List<String>
    ) -> Unit
) {
    if (match == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = HighDensityGreenHeader)
        }
        return
    }

    val teamA = match.teamA
    val teamB = match.teamB

    var tossWinner by remember { mutableStateOf(match.tossWinner ?: teamA) }
    var tossDecision by remember { mutableStateOf(match.tossDecision ?: "BAT") }

    // Pre-fill roster defaults
    val defaultTeamA = listOf("${teamA.replace("Team ", "")} Cap", "Player A1", "Player A2", "Player A3")
    val defaultTeamB = listOf("${teamB.replace("Team ", "")} Cap", "Player B1", "Player B2", "Player B3")

    var teamAPlayer1 by remember { mutableStateOf(existingLineup.filter { it.teamName == teamA }.getOrNull(0)?.playerName ?: defaultTeamA[0]) }
    var teamAPlayer2 by remember { mutableStateOf(existingLineup.filter { it.teamName == teamA }.getOrNull(1)?.playerName ?: defaultTeamA[1]) }
    var teamAPlayer3 by remember { mutableStateOf(existingLineup.filter { it.teamName == teamA }.getOrNull(2)?.playerName ?: defaultTeamA[2]) }
    var teamAPlayer4 by remember { mutableStateOf(existingLineup.filter { it.teamName == teamA }.getOrNull(3)?.playerName ?: defaultTeamA[3]) }

    var teamBPlayer1 by remember { mutableStateOf(existingLineup.filter { it.teamName == teamB }.getOrNull(0)?.playerName ?: defaultTeamB[0]) }
    var teamBPlayer2 by remember { mutableStateOf(existingLineup.filter { it.teamName == teamB }.getOrNull(1)?.playerName ?: defaultTeamB[1]) }
    var teamBPlayer3 by remember { mutableStateOf(existingLineup.filter { it.teamName == teamB }.getOrNull(2)?.playerName ?: defaultTeamB[2]) }
    var teamBPlayer4 by remember { mutableStateOf(existingLineup.filter { it.teamName == teamB }.getOrNull(3)?.playerName ?: defaultTeamB[3]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Setup & Toss", fontWeight = FontWeight.Bold) },
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
                .testTag("match_setup_screen"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Match Header
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HighDensityGreenHeader),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "MATCH #${match.matchNumber}", fontSize = 11.sp, color = GoldAccent, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "$teamA vs $teamB", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }

            // Toss Winner Selection
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "🪙 1. Who Won The Toss?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HighDensityGreenHeader)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TossOptionButton(
                                title = teamA,
                                isSelected = (tossWinner == teamA),
                                onClick = { tossWinner = teamA },
                                modifier = Modifier.weight(1f).testTag("toss_winner_team_a")
                            )
                            TossOptionButton(
                                title = teamB,
                                isSelected = (tossWinner == teamB),
                                onClick = { tossWinner = teamB },
                                modifier = Modifier.weight(1f).testTag("toss_winner_team_b")
                            )
                        }
                    }
                }
            }

            // Toss Decision Selection
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "🏏 2. Toss Winner Chose To:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HighDensityGreenHeader)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TossOptionButton(
                                title = "BAT FIRST",
                                isSelected = (tossDecision == "BAT"),
                                onClick = { tossDecision = "BAT" },
                                modifier = Modifier.weight(1f).testTag("toss_decision_bat")
                            )
                            TossOptionButton(
                                title = "BOWL FIRST",
                                isSelected = (tossDecision == "BOWL"),
                                onClick = { tossDecision = "BOWL" },
                                modifier = Modifier.weight(1f).testTag("toss_decision_bowl")
                            )
                        }
                    }
                }
            }

            // Team A Player
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "👤 $teamA Player (1 Player)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(value = teamAPlayer1, onValueChange = { teamAPlayer1 = it }, label = { Text("Player Name") }, modifier = Modifier.fillMaxWidth().testTag("team_a_p1"))
                    }
                }
            }

            // Team B Player
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "👤 $teamB Player (1 Player)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(value = teamBPlayer1, onValueChange = { teamBPlayer1 = it }, label = { Text("Player Name") }, modifier = Modifier.fillMaxWidth().testTag("team_b_p1"))
                    }
                }
            }

            // Confirm Button
            item {
                Button(
                    onClick = {
                        val teamAPlayers = listOf(teamAPlayer1.ifBlank { teamA })
                        val teamBPlayers = listOf(teamBPlayer1.ifBlank { teamB })

                        onConfirmSetup(tossWinner, tossDecision, teamAPlayers, teamBPlayers)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HighDensityGreenHeader),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_match_setup_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "START LIVE SCORING", fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun TossOptionButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) GoldAccent else CardBorderGreen,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.Black else Color.White
            )
        }
    }
}
