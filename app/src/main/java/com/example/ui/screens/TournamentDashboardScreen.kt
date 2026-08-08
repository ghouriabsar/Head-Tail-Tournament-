package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.MatchEntity
import com.example.data.db.TournamentEntity
import com.example.model.TeamPointsRow
import com.example.ui.components.PointsBarChart
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDashboardScreen(
    tournament: TournamentEntity?,
    matches: List<MatchEntity>,
    pointsTable: List<TeamPointsRow>,
    onNavigate: (String) -> Unit,
    onStartMatch: (Long) -> Unit
) {
    val completedMatches = matches.count { it.status == "COMPLETED" }
    val totalMatches = matches.size.coerceAtLeast(1)
    val remainingMatches = (matches.size - completedMatches).coerceAtLeast(0)
    val progress = (completedMatches.toFloat() / totalMatches.toFloat()).coerceIn(0f, 1f)

    val currentLeader = pointsTable.firstOrNull()?.teamName ?: "TBD"
    val nextMatch = matches.firstOrNull { it.status == "LIVE" } ?: matches.firstOrNull { it.status == "SCHEDULED" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.app_launcher_icon_1786148477723),
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = tournament?.name ?: "Head & Tail",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "TOURNAMENT MANAGER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HighDensityGreenHeader),
                actions = {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "LIVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { onNavigate("settings") },
                        modifier = Modifier.testTag("dashboard_settings_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = HighDensityBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp)
                .testTag("tournament_dashboard_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Champion Banner if tournament complete
            if (tournament?.status == "COMPLETED" && tournament.championTeam != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = HighDensityGreenHeader),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().clickable { onNavigate("champion_celebration") }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "🏆 CHAMPION DECLARED!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                                Text(
                                    text = tournament.championTeam,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Tournament Progress Header Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = (tournament?.name ?: "Premier League 2024").uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HighDensityGreenHeader,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = currentLeader,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = HighDensityTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "is leading",
                                        fontSize = 13.sp,
                                        color = HighDensityTextSecondary,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Progress",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = HighDensityTextSecondary
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HighDensityGreenHeader
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = HighDensityGreenHeader,
                            trackColor = HighDensityProgressTrack
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatMiniBadge(
                                label = "Teams",
                                value = "${pointsTable.size.coerceAtLeast(4)}".padStart(2, '0'),
                                modifier = Modifier.weight(1f)
                            )
                            StatMiniBadge(
                                label = "Matches",
                                value = "$totalMatches".padStart(2, '0'),
                                modifier = Modifier.weight(1f)
                            )
                            StatMiniBadge(
                                label = "Left",
                                value = "$remainingMatches".padStart(2, '0'),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Next Match Spotlight Card
            if (nextMatch != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = HighDensitySurfaceVariant),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, HighDensityBorderDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "NEXT FIXTURE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HighDensityTextSecondary,
                                    letterSpacing = 1.sp
                                )
                                Surface(
                                    color = Color.White.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (nextMatch.status == "LIVE") "LIVE NOW" else "MATCH #${nextMatch.matchNumber}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HighDensityGreenHeader,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(18.dp),
                                border = BorderStroke(1.dp, HighDensityBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Team A
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                color = HighDensityGreenHeader,
                                                shape = CircleShape,
                                                modifier = Modifier.size(46.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = nextMatch.teamA.take(1).uppercase(),
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = nextMatch.teamA,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = HighDensityTextPrimary
                                            )
                                        }

                                        // VS
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        ) {
                                            Text(
                                                text = "VS",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black,
                                                color = HighDensityProgressTrack
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                color = HighDensityGreenHeader,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "MATCH ${nextMatch.matchNumber}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        // Team B
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                color = HighDensitySecondaryPill,
                                                shape = CircleShape,
                                                modifier = Modifier.size(46.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = nextMatch.teamB.take(1).uppercase(),
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = nextMatch.teamB,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = HighDensityTextPrimary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = { onStartMatch(nextMatch.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = HighDensityGreenHeader),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("dashboard_start_next_match_button")
                                    ) {
                                        Icon(
                                            imageVector = if (nextMatch.status == "LIVE") Icons.Default.PlayArrow else Icons.Default.SportsCricket,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (nextMatch.status == "LIVE") "CONTINUE LIVE MATCH" else "START MATCH",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Bar Chart
            item {
                PointsBarChart(pointsTable = pointsTable)
            }

            // Dashboard Grid Nav Buttons
            item {
                Text(
                    text = "Tournament Center",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighDensityTextPrimary,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                val menuItems = listOf(
                    DashboardMenuItem("Live Scoring", Icons.Default.PlayCircle, "live_match_shortcut", HighDensityGreenHeader, isPrimary = true),
                    DashboardMenuItem("Points Table", Icons.Default.Leaderboard, "points_table", HighDensityTextPrimary),
                    DashboardMenuItem("Fixtures", Icons.Default.Event, "fixtures", HighDensityTextPrimary),
                    DashboardMenuItem("Statistics", Icons.Default.Equalizer, "leaderboards", HighDensityTextPrimary),
                    DashboardMenuItem("Players", Icons.Default.Group, "players", HighDensityTextPrimary),
                    DashboardMenuItem("Match History", Icons.Default.History, "match_history", HighDensityTextPrimary),
                    DashboardMenuItem("Tournament History", Icons.Default.Folder, "tournament_history", HighDensityTextPrimary),
                    DashboardMenuItem("New Tournament", Icons.Default.AddCircle, "new_tournament", HighDensityGreenHeader),
                    DashboardMenuItem("Settings", Icons.Default.Settings, "settings", HighDensityTextSecondary)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    items(menuItems) { item ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.isPrimary) HighDensityGreenHeader else MaterialTheme.colorScheme.surface
                            ),
                            border = if (item.isPrimary) null else BorderStroke(1.dp, HighDensityBorder),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp)
                                .clickable {
                                    if (item.route == "live_match_shortcut" && nextMatch != null) {
                                        onStartMatch(nextMatch.id)
                                    } else {
                                        onNavigate(item.route)
                                    }
                                }
                                .testTag("nav_card_${item.route}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (item.isPrimary) Color.White else item.tint,
                                    modifier = Modifier.size(26.dp)
                                )
                                Text(
                                    text = item.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isPrimary) Color.White else HighDensityTextPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMiniBadge(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = HighDensityChipBg,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = HighDensityTextSecondary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = HighDensityTextPrimary
            )
        }
    }
}

private data class DashboardMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val tint: Color,
    val isPrimary: Boolean = false
)

