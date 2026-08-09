package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.BallEntity
import com.example.data.db.MatchEntity
import com.example.data.db.MatchPlayerEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScoringScreen(
    match: MatchEntity?,
    balls: List<BallEntity>,
    lineup: List<MatchPlayerEntity>,
    onBack: () -> Unit,
    onRecordBall: (
        runs: Int,
        isWicket: Boolean,
        dismissalType: String?,
        dismissedPlayer: String?,
        extraType: String?,
        batsman: String,
        nonStriker: String,
        bowler: String,
        battingTeam: String
    ) -> Unit,
    onUndoLastBall: () -> Unit,
    onCompleteInnings: () -> Unit,
    onTriggerDrawOptions: () -> Unit
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

    // Determine current innings batting & bowling team
    val tossWinner = match.tossWinner ?: teamA
    val tossDecision = match.tossDecision ?: "BAT"
    val firstInningsBattingTeam = if (tossDecision == "BAT") tossWinner else (if (tossWinner == teamA) teamB else teamA)
    val firstInningsBowlingTeam = if (firstInningsBattingTeam == teamA) teamB else teamA

    val currentInnings = match.currentInnings
    val currentBattingTeam = if (currentInnings == 1) firstInningsBattingTeam else firstInningsBowlingTeam
    val currentBowlingTeam = if (currentInnings == 1) firstInningsBowlingTeam else firstInningsBattingTeam

    val battingRoster = lineup.filter { it.teamName == currentBattingTeam }.map { it.playerName }.ifEmpty {
        listOf(currentBattingTeam)
    }
    val bowlingRoster = lineup.filter { it.teamName == currentBowlingTeam }.map { it.playerName }.ifEmpty {
        listOf(currentBowlingTeam)
    }

    // Active players state
    var striker by remember(currentInnings) { mutableStateOf(battingRoster.getOrElse(0) { currentBattingTeam }) }
    var nonStriker by remember(currentInnings) { mutableStateOf(battingRoster.getOrElse(1) { battingRoster.getOrElse(0) { currentBattingTeam } }) }
    var bowler by remember(currentInnings) { mutableStateOf(bowlingRoster.getOrElse(0) { currentBowlingTeam }) }

    val swapStrikeLambda = {
        val temp = striker
        striker = nonStriker
        nonStriker = temp
    }

    var showWicketDialog by remember { mutableStateOf(false) }
    var showBowlerDialog by remember { mutableStateOf(false) }

    val currentInningsBalls = balls.filter { it.innings == currentInnings }
    val totalRuns = currentInningsBalls.sumOf { it.runs }
    val totalWickets = currentInningsBalls.count { it.isWicket }
    val maxWickets = battingRoster.size.coerceAtLeast(1)
    val legalDeliveries = currentInningsBalls.count { it.extraType != "WIDE" && it.extraType != "NO_BALL" }

    val currentOverNumber = legalDeliveries / 6
    val currentBallInOver = legalDeliveries % 6

    val oversDisplay = "${legalDeliveries / 6}.${legalDeliveries % 6}"
    val runRate = if (legalDeliveries > 0) (totalRuns.toDouble() / legalDeliveries) * 6.0 else 0.0

    // Target calculation for 2nd Innings
    val targetScore = if (currentInnings >= 2) {
        val innings1Balls = balls.filter { it.innings == 1 }
        innings1Balls.sumOf { it.runs } + 1
    } else 0

    val runsNeeded = (targetScore - totalRuns).coerceAtLeast(0)

    // Check innings or match finish
    LaunchedEffect(totalWickets, totalRuns, currentInnings) {
        if (currentInnings == 1 && totalWickets >= maxWickets && currentInningsBalls.isNotEmpty()) {
            onCompleteInnings()
        } else if (currentInnings >= 2) {
            if (totalWickets >= maxWickets) {
                if (totalRuns == targetScore - 1) {
                    onTriggerDrawOptions()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Scoring", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (currentInnings == 1) {
                        TextButton(
                            onClick = onCompleteInnings,
                            modifier = Modifier.testTag("end_1st_innings_button")
                        ) {
                            Text("END INNINGS", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    IconButton(
                        onClick = onUndoLastBall,
                        modifier = Modifier.testTag("undo_last_ball_button")
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HighDensityGreenHeader, titleContentColor = Color.White)
            )
        },
        containerColor = HighDensityBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp)
                .testTag("live_scoring_screen"),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Score Board Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = HighDensityGreenHeader),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = currentBattingTeam.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldAccent, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "$totalRuns", fontSize = 44.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text(text = "/$totalWickets", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = HighDensitySurfaceVariant)
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(text = "($oversDisplay ov)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(text = "CRR: ${String.format("%.2f", runRate)}", fontSize = 12.sp, color = Color.White)
                            if (currentInnings >= 2) {
                                Text(text = "Target: $targetScore", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                Text(text = "Need $runsNeeded run${if (runsNeeded == 1) "" else "s"}", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }

                // Active Batsman & Bowler Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, HighDensityBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "🏏 Striker: $striker", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HighDensityGreenHeader)
                            Text(text = "🏃 Non-Striker: $nonStriker", fontSize = 12.sp, color = HighDensityTextSecondary)
                            Text(text = "🎯 Bowler: $bowler", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HighDensityTextPrimary)
                        }
                        Row {
                            IconButton(
                                onClick = swapStrikeLambda,
                                modifier = Modifier.testTag("swap_strike_button")
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = "Swap Strike", tint = HighDensityGreenHeader)
                            }
                            IconButton(
                                onClick = { showBowlerDialog = true },
                                modifier = Modifier.testTag("change_bowler_button")
                            ) {
                                Icon(Icons.Default.SportsCricket, contentDescription = "Change Bowler", tint = HighDensityGreenHeader)
                            }
                        }
                    }
                }

                // Ball-by-ball timeline for current over
                Text(text = "Current Over Balls:", fontSize = 12.sp, color = TextSecondaryDark)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val currentOverBalls = currentInningsBalls.takeLast(currentBallInOver.coerceAtLeast(1))
                    items(currentOverBalls) { ball ->
                        BallPill(ball = ball)
                    }
                }
            }

            // Scoring Action Buttons Grid or Match Completed Banner
            if (match.status == "COMPLETED") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().testTag("match_completed_banner")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🏆 MATCH COMPLETED", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = match.winningMargin ?: "Match Finished", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = HighDensityGreenHeader),
                            modifier = Modifier.fillMaxWidth().testTag("match_completed_back_button")
                        ) {
                            Text("Return to Tournament", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScoringButton("1", modifier = Modifier.weight(1f).testTag("btn_1")) {
                            onRecordBall(1, false, null, null, null, striker, nonStriker, bowler, currentBattingTeam)
                            handlePostBallRotation(1, currentBallInOver, swapStrike = swapStrikeLambda)
                        }
                        ScoringButton("2", modifier = Modifier.weight(1f).testTag("btn_2")) {
                            onRecordBall(2, false, null, null, null, striker, nonStriker, bowler, currentBattingTeam)
                            handlePostBallRotation(2, currentBallInOver, swapStrike = swapStrikeLambda)
                        }
                        ScoringButton("3", modifier = Modifier.weight(1f).testTag("btn_3")) {
                            onRecordBall(3, false, null, null, null, striker, nonStriker, bowler, currentBattingTeam)
                            handlePostBallRotation(3, currentBallInOver, swapStrike = swapStrikeLambda)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScoringButton("4", color = FourBlue, modifier = Modifier.weight(1f).testTag("btn_4")) {
                            onRecordBall(4, false, null, null, null, striker, nonStriker, bowler, currentBattingTeam)
                            handlePostBallRotation(4, currentBallInOver, swapStrike = swapStrikeLambda)
                        }
                        ScoringButton("5", modifier = Modifier.weight(1f).testTag("btn_5")) {
                            onRecordBall(5, false, null, null, null, striker, nonStriker, bowler, currentBattingTeam)
                            handlePostBallRotation(5, currentBallInOver, swapStrike = swapStrikeLambda)
                        }
                        ScoringButton("6", color = SixPurple, modifier = Modifier.weight(1f).testTag("btn_6")) {
                            onRecordBall(6, false, null, null, null, striker, nonStriker, bowler, currentBattingTeam)
                            handlePostBallRotation(6, currentBallInOver, swapStrike = swapStrikeLambda)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ScoringButton("OUT / WICKET", color = BoundaryRed, modifier = Modifier.fillMaxWidth().testTag("btn_out")) {
                            onRecordBall(0, true, "BOWLED", striker, null, striker, nonStriker, bowler, currentBattingTeam)
                            val remaining = battingRoster.filter { it != striker }
                            if (remaining.isNotEmpty()) striker = remaining.first()
                        }
                    }
                }
            }
        }
    }

    // Wicket Dialog
    if (showWicketDialog) {
        AlertDialog(
            onDismissRequest = { showWicketDialog = false },
            title = { Text("⚡ WICKET / OUT") },
            text = {
                Column {
                    Text("Select who is out:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onRecordBall(0, true, "BOWLED", striker, null, striker, nonStriker, bowler, currentBattingTeam)
                            showWicketDialog = false
                            // Prompt for next batsman
                            val remaining = battingRoster.filter { it != striker && it != nonStriker }
                            if (remaining.isNotEmpty()) striker = remaining.first()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BoundaryRed),
                        modifier = Modifier.fillMaxWidth().testTag("wicket_striker_out")
                    ) {
                        Text("Striker: $striker")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            onRecordBall(0, true, "RUN_OUT", nonStriker, null, striker, nonStriker, bowler, currentBattingTeam)
                            showWicketDialog = false
                            val remaining = battingRoster.filter { it != striker && it != nonStriker }
                            if (remaining.isNotEmpty()) nonStriker = remaining.first()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BoundaryRed),
                        modifier = Modifier.fillMaxWidth().testTag("wicket_non_striker_out")
                    ) {
                        Text("Non-Striker: $nonStriker")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showWicketDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Change Bowler Dialog
    if (showBowlerDialog) {
        AlertDialog(
            onDismissRequest = { showBowlerDialog = false },
            title = { Text("Select Bowler") },
            text = {
                Column {
                    bowlingRoster.forEach { bName ->
                        Surface(
                            color = if (bowler == bName) HighDensityGreenHeader else HighDensityChipBg,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    bowler = bName
                                    showBowlerDialog = false
                                }
                        ) {
                            Text(
                                text = bName,
                                color = if (bowler == bName) Color.White else HighDensityTextPrimary,
                                modifier = Modifier.padding(12.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showBowlerDialog = false }) { Text("Close") } }
        )
    }
}

private fun handlePostBallRotation(
    runs: Int,
    currentBallInOver: Int,
    swapStrike: () -> Unit
) {
    if (runs == 1 || runs == 3 || runs == 5) {
        swapStrike()
    }
    if (currentBallInOver == 5) { // 6th ball
        swapStrike()
    }
}

@Composable
fun ScoringButton(
    text: String,
    color: Color = HighDensityGreenHeader,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, HighDensityBorderDark),
        modifier = modifier
            .height(52.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = textColor
            )
        }
    }
}

@Composable
fun BallPill(ball: BallEntity) {
    val bgColor = when {
        ball.isWicket -> BoundaryRed
        ball.runs == 6 -> SixPurple
        ball.runs == 4 -> FourBlue
        else -> PitchGreenPrimary
    }

    val text = when {
        ball.isWicket -> "W"
        ball.extraType != null -> "${ball.extraType.take(1)}+${ball.runs}"
        else -> "${ball.runs}"
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
    }
}
