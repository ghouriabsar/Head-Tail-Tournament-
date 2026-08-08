package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTournamentScreen(
    onBack: () -> Unit,
    onCreateTournament: (name: String, teams: List<Pair<String, String>>) -> Unit
) {
    var tournamentName by remember { mutableStateOf("Head & Tail League 2026") }

    var team1Name by remember { mutableStateOf("Team Absar") }
    var team1Cap by remember { mutableStateOf("Absar") }

    var team2Name by remember { mutableStateOf("Team Zubair") }
    var team2Cap by remember { mutableStateOf("Zubair") }

    var team3Name by remember { mutableStateOf("Team Moiz") }
    var team3Cap by remember { mutableStateOf("Moiz") }

    var team4Name by remember { mutableStateOf("Team Usman") }
    var team4Cap by remember { mutableStateOf("Usman") }

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
                        Text(text = "ℹ️ NEW TOURNAMENT CREATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Creating a new tournament will generate fresh fixtures and reset current standings. All past tournament archives and lifetime player career statistics will be preserved permanently!", fontSize = 12.sp, color = Color.White)
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
                        Text(text = "🏆 Tournament Title", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HighDensityGreenHeader)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tournamentName,
                            onValueChange = { tournamentName = it },
                            label = { Text("Tournament Name") },
                            modifier = Modifier.fillMaxWidth().testTag("new_tournament_name_input")
                        )
                    }
                }
            }

            // Team Inputs
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "👥 Participating Teams (Double Round Robin)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(value = team1Name, onValueChange = { team1Name = it }, label = { Text("Team 1 Name") }, modifier = Modifier.fillMaxWidth().testTag("new_team1_name"))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = team1Cap, onValueChange = { team1Cap = it }, label = { Text("Team 1 Captain") }, modifier = Modifier.fillMaxWidth().testTag("new_team1_cap"))

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(value = team2Name, onValueChange = { team2Name = it }, label = { Text("Team 2 Name") }, modifier = Modifier.fillMaxWidth().testTag("new_team2_name"))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = team2Cap, onValueChange = { team2Cap = it }, label = { Text("Team 2 Captain") }, modifier = Modifier.fillMaxWidth().testTag("new_team2_cap"))

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(value = team3Name, onValueChange = { team3Name = it }, label = { Text("Team 3 Name") }, modifier = Modifier.fillMaxWidth().testTag("new_team3_name"))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = team3Cap, onValueChange = { team3Cap = it }, label = { Text("Team 3 Captain") }, modifier = Modifier.fillMaxWidth().testTag("new_team3_cap"))

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(value = team4Name, onValueChange = { team4Name = it }, label = { Text("Team 4 Name") }, modifier = Modifier.fillMaxWidth().testTag("new_team4_name"))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = team4Cap, onValueChange = { team4Cap = it }, label = { Text("Team 4 Captain") }, modifier = Modifier.fillMaxWidth().testTag("new_team4_cap"))
                    }
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
                        val teamList = listOf(
                            Pair(team1Name, team1Cap),
                            Pair(team2Name, team2Cap),
                            Pair(team3Name, team3Cap),
                            Pair(team4Name, team4Cap)
                        ).filter { it.first.isNotBlank() }

                        onCreateTournament(tournamentName, teamList)
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
