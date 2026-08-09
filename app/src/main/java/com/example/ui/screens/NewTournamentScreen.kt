package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class TeamItemState(
    val name: String,
    val captain: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTournamentScreen(
    onBack: () -> Unit,
    onCreateTournament: (name: String, teams: List<Pair<String, String>>) -> Unit
) {
    var tournamentName by remember { mutableStateOf("Head & Tail League 2026") }

    val teamsList = remember {
        mutableStateListOf(
            TeamItemState("Team Absar", "Absar"),
            TeamItemState("Team Zubair", "Zubair"),
            TeamItemState("Team Moiz", "Moiz"),
            TeamItemState("Team Usman", "Usman")
        )
    }

    var showConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Tournament", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HighDensityGreenHeader,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = HighDensityBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp)
                .testTag("new_tournament_screen"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Info Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HighDensityGreenHeader),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ℹ️ NEW TOURNAMENT CREATION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Creating a new tournament will generate fresh fixtures and reset current standings. Add or remove teams below as needed!",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Tournament Name
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🏆 Tournament Title",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighDensityGreenHeader
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tournamentName,
                            onValueChange = { tournamentName = it },
                            label = { Text("Tournament Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_tournament_name_input")
                        )
                    }
                }
            }

            // Teams Header
            item {
                Text(
                    text = "👥 Participating Teams (${teamsList.size} Teams)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighDensityTextPrimary
                )
            }

            itemsIndexed(teamsList) { index, team ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Team #${index + 1}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HighDensityGreenHeader
                            )
                            if (teamsList.size > 2) {
                                IconButton(
                                    onClick = { teamsList.removeAt(index) },
                                    modifier = Modifier.testTag("remove_team_$index")
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Remove Team",
                                        tint = BoundaryRed
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = team.name,
                            onValueChange = { newName ->
                                teamsList[index] = team.copy(name = newName)
                            },
                            label = { Text("Team Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_team_${index}_name")
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = team.captain,
                            onValueChange = { newCap ->
                                teamsList[index] = team.copy(captain = newCap)
                            },
                            label = { Text("Player / Captain Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_team_${index}_cap")
                        )
                    }
                }
            }

            // Add Team Button
            item {
                OutlinedButton(
                    onClick = {
                        val nextNum = teamsList.size + 1
                        teamsList.add(TeamItemState("Team $nextNum", "Player $nextNum"))
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, HighDensityGreenHeader),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_team_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Team", tint = HighDensityGreenHeader)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ADD ANOTHER TEAM", fontWeight = FontWeight.Bold, color = HighDensityGreenHeader)
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = { showConfirmation = true },
                    colors = ButtonDefaults.buttonColors(containerColor = HighDensityGreenHeader),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("create_tournament_submit_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "GENERATE NEW TOURNAMENT", fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("Start a new tournament?") },
            text = { Text("Old tournament history and lifetime player career statistics will be kept safe in the database.") },
            confirmButton = {
                Button(
                    onClick = {
                        val finalTeams = teamsList
                            .filter { it.name.isNotBlank() }
                            .map { Pair(it.name.trim(), it.captain.ifBlank { it.name }.trim()) }

                        onCreateTournament(tournamentName, finalTeams)
                        showConfirmation = false
                    },
                    modifier = Modifier.testTag("confirm_create_tournament_btn")
                ) {
                    Text("Confirm & Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) { Text("Cancel") }
            }
        )
    }
}
