package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.EventPopupOverlay
import com.example.ui.components.EventType
import com.example.ui.components.FullScreenChampionCelebration
import com.example.ui.screens.*
import com.example.ui.theme.HeadAndTailTheme
import com.example.ui.viewmodel.AnimationEvent
import com.example.ui.viewmodel.TournamentViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TournamentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HeadAndTailTheme {
                val navController = rememberNavController()

                val activeTournament by viewModel.activeTournament.collectAsState()
                val allTournaments by viewModel.allTournaments.collectAsState()
                val matches by viewModel.matches.collectAsState()
                val pointsTable by viewModel.pointsTable.collectAsState()
                val playerStatsList by viewModel.playerStatsList.collectAsState()

                val selectedMatch by viewModel.selectedMatch.collectAsState()
                val ballsForSelectedMatch by viewModel.ballsForSelectedMatch.collectAsState()
                val lineupForSelectedMatch by viewModel.lineupForSelectedMatch.collectAsState()

                val animationEvent by viewModel.animationEvent.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    navController.navigate("dashboard") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("dashboard") {
                            TournamentDashboardScreen(
                                tournament = activeTournament,
                                matches = matches,
                                pointsTable = pointsTable,
                                onNavigate = { route -> navController.navigate(route) },
                                onStartMatch = { matchId ->
                                    viewModel.selectMatch(matchId)
                                    navController.navigate("match_setup/$matchId")
                                }
                            )
                        }

                        composable("fixtures") {
                            FixturesScreen(
                                matches = matches,
                                onBack = { navController.popBackStack() },
                                onOpenMatch = { matchId ->
                                    viewModel.selectMatch(matchId)
                                    val m = matches.find { it.id == matchId }
                                    if (m?.status == "COMPLETED") {
                                        navController.navigate("match_detail/$matchId")
                                    } else {
                                        navController.navigate("match_setup/$matchId")
                                    }
                                }
                            )
                        }

                        composable(
                            route = "match_setup/{matchId}",
                            arguments = listOf(navArgument("matchId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val matchId = backStackEntry.arguments?.getLong("matchId") ?: return@composable
                            LaunchedEffect(matchId) { viewModel.selectMatch(matchId) }

                            MatchSetupScreen(
                                match = selectedMatch,
                                existingLineup = lineupForSelectedMatch,
                                onBack = { navController.popBackStack() },
                                onConfirmSetup = { tossWinner, tossDecision, teamAPlayers, teamBPlayers ->
                                    viewModel.updateTossAndLineup(matchId, tossWinner, tossDecision, teamAPlayers, teamBPlayers)
                                    navController.navigate("live_scoring/$matchId") {
                                        popUpTo("match_setup/$matchId") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "live_scoring/{matchId}",
                            arguments = listOf(navArgument("matchId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val matchId = backStackEntry.arguments?.getLong("matchId") ?: return@composable
                            LaunchedEffect(matchId) { viewModel.selectMatch(matchId) }

                            LiveScoringScreen(
                                match = selectedMatch,
                                balls = ballsForSelectedMatch,
                                lineup = lineupForSelectedMatch,
                                onBack = { navController.popBackStack() },
                                onRecordBall = { runs, isWicket, dismissalType, dismissedPlayer, extraType, batsman, nonStriker, bowler, battingTeam ->
                                    val overNumber = ballsForSelectedMatch.size / 6
                                    val ballNumber = (ballsForSelectedMatch.size % 6) + 1
                                    val currentInnings = selectedMatch?.currentInnings ?: 1

                                    viewModel.recordBall(
                                        matchId = matchId,
                                        innings = currentInnings,
                                        overNumber = overNumber,
                                        ballNumber = ballNumber,
                                        batsmanName = batsman,
                                        nonStrikerName = nonStriker,
                                        bowlerName = bowler,
                                        battingTeam = battingTeam,
                                        runs = runs,
                                        isWicket = isWicket,
                                        dismissalType = dismissalType,
                                        dismissedPlayer = dismissedPlayer,
                                        extraType = extraType
                                    )
                                },
                                onUndoLastBall = { viewModel.undoLastBall(matchId) },
                                onCompleteInnings = { navController.navigate("innings_complete/$matchId") },
                                onTriggerDrawOptions = { viewModel.resolveDrawWithPoints(matchId) }
                            )
                        }

                        composable(
                            route = "innings_complete/{matchId}",
                            arguments = listOf(navArgument("matchId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val matchId = backStackEntry.arguments?.getLong("matchId") ?: return@composable

                            val innings1Balls = ballsForSelectedMatch.filter { it.innings == 1 }
                            val totalRuns = innings1Balls.sumOf { it.runs }
                            val wickets = innings1Balls.count { it.isWicket }
                            val overs = "${innings1Balls.size / 6}.${innings1Balls.size % 6}"

                            val firstInningsBattingTeam = selectedMatch?.let { m ->
                                val winner = m.tossWinner
                                val decision = m.tossDecision
                                if ((winner == m.teamA && decision == "BAT") || (winner == m.teamB && decision == "BOWL")) {
                                    m.teamA
                                } else {
                                    m.teamB
                                }
                            } ?: (selectedMatch?.teamA ?: "Batting Team")

                            InningsCompleteScreen(
                                battingTeam = firstInningsBattingTeam,
                                totalRuns = totalRuns,
                                wickets = wickets,
                                overs = overs,
                                targetScore = totalRuns + 1,
                                onStartSecondInnings = {
                                    viewModel.startSecondInnings(matchId)
                                    navController.navigate("live_scoring/$matchId") {
                                        popUpTo("innings_complete/$matchId") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "super_over/{matchId}",
                            arguments = listOf(navArgument("matchId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val matchId = backStackEntry.arguments?.getLong("matchId") ?: return@composable

                            SuperOverScreen(
                                teamA = selectedMatch?.teamA ?: "Team A",
                                teamB = selectedMatch?.teamB ?: "Team B",
                                onSelectWinner = { winner ->
                                    viewModel.completeSuperOver(matchId, winner)
                                    navController.navigate("dashboard") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("points_table") {
                            PointsTableScreen(
                                pointsTable = pointsTable,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("players") {
                            PlayersScreen(
                                playerStats = playerStatsList,
                                onBack = { navController.popBackStack() },
                                onSelectPlayer = { playerName ->
                                    navController.navigate("player_detail/$playerName")
                                },
                                onAddPlayer = { _, _ -> viewModel.refreshPlayerStats() }
                            )
                        }

                        composable(
                            route = "player_detail/{playerName}",
                            arguments = listOf(navArgument("playerName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val pName = backStackEntry.arguments?.getString("playerName") ?: ""
                            val pStats = playerStatsList.find { it.playerName == pName }

                            PlayerDetailScreen(
                                stats = pStats,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("leaderboards") {
                            LeaderboardsScreen(
                                playerStats = playerStatsList,
                                onBack = { navController.popBackStack() },
                                onSelectPlayer = { playerName -> navController.navigate("player_detail/$playerName") }
                            )
                        }

                        composable("match_history") {
                            MatchHistoryScreen(
                                matches = matches,
                                onBack = { navController.popBackStack() },
                                onSelectMatch = { matchId -> navController.navigate("match_detail/$matchId") }
                            )
                        }

                        composable(
                            route = "match_detail/{matchId}",
                            arguments = listOf(navArgument("matchId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val matchId = backStackEntry.arguments?.getLong("matchId") ?: return@composable
                            LaunchedEffect(matchId) { viewModel.selectMatch(matchId) }

                            MatchDetailScreen(
                                match = selectedMatch,
                                balls = ballsForSelectedMatch,
                                onBack = { navController.popBackStack() },
                                onDeleteBall = { ballId -> viewModel.deleteBall(ballId, matchId) }
                            )
                        }

                        composable("tournament_history") {
                            TournamentHistoryScreen(
                                tournaments = allTournaments,
                                onBack = { navController.popBackStack() },
                                onSelectTournament = { id ->
                                    viewModel.selectTournament(id)
                                    navController.navigate("dashboard")
                                }
                            )
                        }

                        composable("champion_celebration") {
                            FullScreenChampionCelebration(
                                championTeam = activeTournament?.championTeam ?: "Champions",
                                runnerUpTeam = "Runner Up",
                                finalScore = "Tournament Champion",
                                tournamentName = activeTournament?.name ?: "Head & Tail Tournament",
                                onDismiss = { navController.popBackStack() }
                            )
                        }

                        composable("new_tournament") {
                            NewTournamentScreen(
                                onBack = { navController.popBackStack() },
                                onCreateTournament = { name, teams ->
                                    viewModel.createNewTournament(name, teams)
                                    navController.navigate("dashboard") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onExportBackup = { viewModel.exportDataJson() },
                                onResetKeepPlayerStats = { viewModel.resetTournamentKeepingPlayerStats() }
                            )
                        }
                    }

                    // Floating Event Popup Overlays (FOUR, SIX, WICKET)
                    if (animationEvent != null) {
                        EventPopupOverlay(
                            event = when (animationEvent!!) {
                                AnimationEvent.Four -> EventType.FOUR
                                AnimationEvent.Six -> EventType.SIX
                                AnimationEvent.Wicket -> EventType.WICKET
                            },
                            onDismiss = { viewModel.clearAnimationEvent() }
                        )
                    }
                }
            }
        }
    }
}
