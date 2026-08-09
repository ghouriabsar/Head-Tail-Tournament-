package com.example.data.repository

import com.example.data.db.*
import com.example.model.*
import kotlinx.coroutines.flow.*
import org.json.JSONArray
import org.json.JSONObject

class TournamentRepository(private val dao: TournamentDao) {

    val activeTournament: Flow<TournamentEntity?> = dao.getActiveTournament()
    val allTournaments: Flow<List<TournamentEntity>> = dao.getAllTournaments()
    val allPlayers: Flow<List<PlayerEntity>> = dao.getAllPlayers()

    suspend fun initializeDefaultDataIfNeeded() {
        val active = dao.getActiveTournamentSync()
        if (active != null) return

        // 1. Create Default Tournament
        val tournamentId = dao.insertTournament(
            TournamentEntity(
                name = "Head & Tail Cup 2026",
                format = "DOUBLE_ROUND_ROBIN",
                status = "IN_PROGRESS"
            )
        )

        // 2. Default Teams
        val teamNames = listOf("Team Absar", "Team Zubair", "Team Moiz", "Team Usman")
        val captains = listOf("Absar", "Zubair", "Moiz", "Usman")
        val colors = listOf("#1E5128", "#1A365D", "#742A2A", "#7B341E")

        val teams = teamNames.mapIndexed { idx, name ->
            TeamEntity(
                tournamentId = tournamentId,
                name = name,
                captainName = captains[idx],
                colorHex = colors[idx]
            )
        }
        dao.insertTeams(teams)

        // 3. Permanent Players Database
        val defaultPlayers = listOf(
            // Team Absar
            PlayerEntity(name = "Absar", teamName = "Team Absar"),
            PlayerEntity(name = "Ali", teamName = "Team Absar"),
            PlayerEntity(name = "Bilal", teamName = "Team Absar"),
            PlayerEntity(name = "Hamza", teamName = "Team Absar"),
            // Team Zubair
            PlayerEntity(name = "Zubair", teamName = "Team Zubair"),
            PlayerEntity(name = "Fahad", teamName = "Team Zubair"),
            PlayerEntity(name = "Saad", teamName = "Team Zubair"),
            PlayerEntity(name = "Daniyal", teamName = "Team Zubair"),
            // Team Moiz
            PlayerEntity(name = "Moiz", teamName = "Team Moiz"),
            PlayerEntity(name = "Hassan", teamName = "Team Moiz"),
            PlayerEntity(name = "Omer", teamName = "Team Moiz"),
            PlayerEntity(name = "Tariq", teamName = "Team Moiz"),
            // Team Usman
            PlayerEntity(name = "Usman", teamName = "Team Usman"),
            PlayerEntity(name = "Farhan", teamName = "Team Usman"),
            PlayerEntity(name = "Yasir", teamName = "Team Usman"),
            PlayerEntity(name = "Nabeel", teamName = "Team Usman")
        )

        val existingPlayers = dao.getAllPlayersSync()
        if (existingPlayers.isEmpty()) {
            dao.insertPlayers(defaultPlayers)
        }

        // 4. Generate Double Round Robin Fixtures (12 matches)
        generateFixturesForTournament(tournamentId, teamNames)
    }

    private suspend fun generateFixturesForTournament(tournamentId: Long, teams: List<String>) {
        if (teams.size < 2) return

        val matches = mutableListOf<MatchEntity>()
        var matchCount = 1

        // Round 1
        for (i in teams.indices) {
            for (j in i + 1 until teams.size) {
                matches.add(
                    MatchEntity(
                        tournamentId = tournamentId,
                        matchNumber = matchCount++,
                        teamA = teams[i],
                        teamB = teams[j],
                        status = "SCHEDULED"
                    )
                )
            }
        }

        // Round 2 (Reverse fixtures)
        for (i in teams.indices) {
            for (j in i + 1 until teams.size) {
                matches.add(
                    MatchEntity(
                        tournamentId = tournamentId,
                        matchNumber = matchCount++,
                        teamA = teams[j],
                        teamB = teams[i],
                        status = "SCHEDULED"
                    )
                )
            }
        }

        dao.insertMatches(matches)
    }

    suspend fun createNewTournament(name: String, teamList: List<Pair<String, String>>): Long {
        val tournamentId = dao.insertTournament(
            TournamentEntity(
                name = name,
                format = "DOUBLE_ROUND_ROBIN",
                status = "IN_PROGRESS"
            )
        )

        val teams = teamList.map { (teamName, captain) ->
            TeamEntity(
                tournamentId = tournamentId,
                name = teamName,
                captainName = captain
            )
        }
        dao.insertTeams(teams)

        // Ensure captains/players are in permanent players database
        val existing = dao.getAllPlayersSync()
        val existingNames = existing.map { it.name.lowercase().trim() }.toSet()

        val newPlayers = mutableListOf<PlayerEntity>()
        for ((teamName, captain) in teamList) {
            if (captain.isNotBlank() && !existingNames.contains(captain.lowercase().trim())) {
                newPlayers.add(PlayerEntity(name = captain, teamName = teamName))
            }
        }
        if (newPlayers.isNotEmpty()) {
            dao.insertPlayers(newPlayers)
        }

        generateFixturesForTournament(tournamentId, teamList.map { it.first })
        return tournamentId
    }

    fun getMatchesForTournament(tournamentId: Long): Flow<List<MatchEntity>> {
        return dao.getMatchesForTournament(tournamentId)
    }

    fun getTeamsForTournament(tournamentId: Long): Flow<List<TeamEntity>> {
        return dao.getTeamsForTournament(tournamentId)
    }

    fun getMatchById(matchId: Long): Flow<MatchEntity?> {
        return dao.getMatchById(matchId)
    }

    fun getBallsForMatch(matchId: Long): Flow<List<BallEntity>> {
        return dao.getBallsForMatch(matchId)
    }

    fun getLineupForMatch(matchId: Long): Flow<List<MatchPlayerEntity>> {
        return dao.getLineupForMatch(matchId)
    }

    suspend fun getMatchByIdSync(matchId: Long): MatchEntity? = dao.getMatchByIdSync(matchId)
    suspend fun getBallsForMatchSync(matchId: Long): List<BallEntity> = dao.getBallsForMatchSync(matchId)

    suspend fun updateTossAndLineup(
        matchId: Long,
        tossWinner: String,
        tossDecision: String,
        teamAPlayers: List<String>,
        teamBPlayers: List<String>
    ) {
        val match = dao.getMatchByIdSync(matchId) ?: return
        val updated = match.copy(
            tossWinner = tossWinner,
            tossDecision = tossDecision,
            status = "LIVE"
        )
        dao.updateMatch(updated)

        dao.deleteLineupForMatch(matchId)
        val lineup = mutableListOf<MatchPlayerEntity>()
        teamAPlayers.forEach { lineup.add(MatchPlayerEntity(matchId = matchId, teamName = match.teamA, playerName = it)) }
        teamBPlayers.forEach { lineup.add(MatchPlayerEntity(matchId = matchId, teamName = match.teamB, playerName = it)) }
        dao.insertMatchPlayers(lineup)

        // Also add new player names to permanent database if not already there
        val existing = dao.getAllPlayersSync().map { it.name.lowercase().trim() }.toSet()
        val toInsert = mutableListOf<PlayerEntity>()
        teamAPlayers.forEach { if (!existing.contains(it.lowercase().trim())) toInsert.add(PlayerEntity(name = it, teamName = match.teamA)) }
        teamBPlayers.forEach { if (!existing.contains(it.lowercase().trim())) toInsert.add(PlayerEntity(name = it, teamName = match.teamB)) }
        if (toInsert.isNotEmpty()) {
            dao.insertPlayers(toInsert)
        }
    }

    suspend fun recordBall(
        matchId: Long,
        innings: Int,
        overNumber: Int,
        ballNumber: Int,
        batsmanName: String,
        nonStrikerName: String,
        bowlerName: String,
        battingTeam: String,
        runs: Int,
        isWicket: Boolean,
        dismissalType: String? = null,
        dismissedPlayer: String? = null,
        extraType: String? = null
    ): BallEntity {
        val ball = BallEntity(
            matchId = matchId,
            innings = innings,
            overNumber = overNumber,
            ballNumber = ballNumber,
            batsmanName = batsmanName,
            nonStrikerName = nonStrikerName,
            bowlerName = bowlerName,
            battingTeam = battingTeam,
            runs = runs,
            isWicket = isWicket,
            dismissalType = dismissalType,
            dismissedPlayer = dismissedPlayer,
            extraType = extraType
        )
        val ballId = dao.insertBall(ball)

        // Recalculate match state
        recalculateMatchState(matchId)
        return ball.copy(id = ballId)
    }

    suspend fun undoLastBall(matchId: Long) {
        val balls = dao.getBallsForMatchSync(matchId)
        if (balls.isNotEmpty()) {
            val lastBall = balls.last()
            dao.deleteBall(lastBall.id)
            recalculateMatchState(matchId)
        }
    }

    suspend fun deleteBall(ballId: Long, matchId: Long) {
        dao.deleteBall(ballId)
        recalculateMatchState(matchId)
    }

    suspend fun startSecondInnings(matchId: Long) {
        val match = dao.getMatchByIdSync(matchId) ?: return
        val updated = match.copy(currentInnings = 2)
        dao.updateMatch(updated)
        recalculateMatchState(matchId)
    }

    suspend fun recalculateMatchState(matchId: Long) {
        val match = dao.getMatchByIdSync(matchId) ?: return
        val balls = dao.getBallsForMatchSync(matchId)
        val lineup = dao.getLineupForMatchSync(matchId)

        val teamALineupSize = lineup.count { it.teamName == match.teamA }.coerceAtLeast(1)
        val teamBLineupSize = lineup.count { it.teamName == match.teamB }.coerceAtLeast(1)

        var teamAScore = 0
        var teamAWickets = 0
        var teamABalls = 0

        var teamBScore = 0
        var teamBWickets = 0
        var teamBBalls = 0

        val battingTeam1 = getFirstInningsBattingTeam(match)

        for (ball in balls) {
            val isTeamA = (ball.battingTeam == match.teamA)
            if (isTeamA) {
                teamAScore += ball.runs
                if (ball.isWicket) teamAWickets++
                if (ball.extraType != "WIDE" && ball.extraType != "NO_BALL") teamABalls++
            } else {
                teamBScore += ball.runs
                if (ball.isWicket) teamBWickets++
                if (ball.extraType != "WIDE" && ball.extraType != "NO_BALL") teamBBalls++
            }
        }

        var status = match.status
        var winnerTeam = match.winnerTeam
        var isDraw = match.isDraw
        var winningMargin = match.winningMargin

        // Check if match completed naturally
        val maxBalls = match.oversPerInnings * 6
        val isFirstInningsTeamA = (battingTeam1 == match.teamA)

        val maxWicketsTeam1 = if (isFirstInningsTeamA) teamALineupSize else teamBLineupSize
        val maxWicketsTeam2 = if (isFirstInningsTeamA) teamBLineupSize else teamALineupSize

        val innings1Balls = if (isFirstInningsTeamA) teamABalls else teamBBalls
        val innings1Wickets = if (isFirstInningsTeamA) teamAWickets else teamBWickets

        val innings2Balls = if (isFirstInningsTeamA) teamBBalls else teamABalls
        val innings2Wickets = if (isFirstInningsTeamA) teamBWickets else teamAWickets
        val innings1Score = if (isFirstInningsTeamA) teamAScore else teamBScore
        val innings2Score = if (isFirstInningsTeamA) teamBScore else teamAScore

        val secondInningsBattingTeam = if (isFirstInningsTeamA) match.teamB else match.teamA

        // Target chasing
        if (match.currentInnings >= 2) {
            if (innings2Score > innings1Score) {
                // Chasing team won!
                status = "COMPLETED"
                winnerTeam = secondInningsBattingTeam
                isDraw = false
                val wicketsLeft = (maxWicketsTeam2 - innings2Wickets).coerceAtLeast(1)
                winningMargin = "$secondInningsBattingTeam won by $wicketsLeft wicket${if (wicketsLeft > 1) "s" else ""}"
            } else if (innings2Wickets >= maxWicketsTeam2) {
                status = "COMPLETED"
                if (innings1Score > innings2Score) {
                    val marginRuns = innings1Score - innings2Score
                    winnerTeam = battingTeam1
                    isDraw = false
                    winningMargin = "$battingTeam1 won by $marginRuns run${if (marginRuns > 1) "s" else ""}"
                } else if (innings1Score == innings2Score) {
                    // Match Drawn!
                    if (!match.isSuperOver) {
                        isDraw = true
                        winnerTeam = null
                        winningMargin = "Match Drawn (Tie)"
                    }
                }
            }
        }

        val updated = match.copy(
            teamAScore = teamAScore,
            teamAWickets = teamAWickets,
            teamABalls = teamABalls,
            teamBScore = teamBScore,
            teamBWickets = teamBWickets,
            teamBBalls = teamBBalls,
            status = status,
            winnerTeam = winnerTeam,
            isDraw = isDraw,
            winningMargin = winningMargin
        )
        dao.updateMatch(updated)

        checkFinalQualificationIfNeeded(match.tournamentId)
    }

    fun getFirstInningsBattingTeam(match: MatchEntity): String {
        val tossWinner = match.tossWinner ?: match.teamA
        val decision = match.tossDecision ?: "BAT"
        val otherTeam = if (tossWinner == match.teamA) match.teamB else match.teamA

        return if (decision == "BAT") tossWinner else otherTeam
    }

    suspend fun resolveDrawWithPoints(matchId: Long) {
        val match = dao.getMatchByIdSync(matchId) ?: return
        val updated = match.copy(
            status = "COMPLETED",
            isDraw = true,
            winnerTeam = null,
            winningMargin = "Match Drawn (1 Point Each)"
        )
        dao.updateMatch(updated)
        checkFinalQualificationIfNeeded(match.tournamentId)
    }

    suspend fun completeSuperOver(matchId: Long, winnerTeamName: String) {
        val match = dao.getMatchByIdSync(matchId) ?: return
        val updated = match.copy(
            status = "COMPLETED",
            isSuperOver = true,
            superOverWinner = winnerTeamName,
            winnerTeam = winnerTeamName,
            isDraw = false,
            winningMargin = "$winnerTeamName won in Super Over"
        )
        dao.updateMatch(updated)
        checkFinalQualificationIfNeeded(match.tournamentId)
    }

    private suspend fun checkFinalQualificationIfNeeded(tournamentId: Long) {
        val matches = dao.getMatchesForTournamentSync(tournamentId)
        val leagueMatches = matches.filter { !it.isFinal }
        val completedLeagueMatches = leagueMatches.filter { it.status == "COMPLETED" }

        if (completedLeagueMatches.size == leagueMatches.size && leagueMatches.isNotEmpty()) {
            // All league matches complete!
            val standings = calculatePointsTableSync(tournamentId)
            if (standings.size >= 2) {
                val team1 = standings[0].teamName
                val team2 = standings[1].teamName

                val existingFinal = matches.find { it.isFinal }
                if (existingFinal == null) {
                    val finalMatch = MatchEntity(
                        tournamentId = tournamentId,
                        matchNumber = matches.size + 1,
                        teamA = team1,
                        teamB = team2,
                        isFinal = true,
                        status = "SCHEDULED"
                    )
                    dao.insertMatch(finalMatch)
                } else if (existingFinal.status == "COMPLETED") {
                    // Update Tournament Champion!
                    val champion = existingFinal.winnerTeam ?: team1
                    val runnerUp = if (champion == existingFinal.teamA) existingFinal.teamB else existingFinal.teamA
                    val tournament = dao.getActiveTournamentSync()
                    if (tournament != null && tournament.id == tournamentId) {
                        dao.updateTournament(
                            tournament.copy(
                                status = "COMPLETED",
                                championTeam = champion,
                                runnerUpTeam = runnerUp,
                                endDate = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
        }
    }

    fun getPointsTable(tournamentId: Long): Flow<List<TeamPointsRow>> = flow {
        dao.getMatchesForTournament(tournamentId).collect {
            emit(calculatePointsTableSync(tournamentId))
        }
    }

    suspend fun calculatePointsTableSync(tournamentId: Long): List<TeamPointsRow> {
        val teams = dao.getTeamsForTournamentSync(tournamentId)
        val matches = dao.getMatchesForTournamentSync(tournamentId).filter { !it.isFinal && it.status == "COMPLETED" }

        val rowMap = teams.associate { it.name to TeamPointsRow(teamName = it.name) }.toMutableMap()

        for (match in matches) {
            val teamA = match.teamA
            val teamB = match.teamB

            val rowA = rowMap[teamA] ?: TeamPointsRow(teamName = teamA)
            val rowB = rowMap[teamB] ?: TeamPointsRow(teamName = teamB)

            var playedA = rowA.played + 1
            var wonA = rowA.won
            var drawA = rowA.draw
            var lostA = rowA.lost
            var pointsA = rowA.points

            var playedB = rowB.played + 1
            var wonB = rowB.won
            var drawB = rowB.draw
            var lostB = rowB.lost
            var pointsB = rowB.points

            if (match.isDraw) {
                drawA++
                drawB++
                pointsA += 1
                pointsB += 1
            } else if (match.winnerTeam == teamA) {
                wonA++
                lostB++
                pointsA += 2
            } else if (match.winnerTeam == teamB) {
                wonB++
                lostA++
                pointsB += 2
            }

            rowMap[teamA] = rowA.copy(
                played = playedA,
                won = wonA,
                draw = drawA,
                lost = lostA,
                points = pointsA,
                runsFor = rowA.runsFor + match.teamAScore,
                ballsFor = rowA.ballsFor + match.teamABalls,
                runsAgainst = rowA.runsAgainst + match.teamBScore,
                ballsAgainst = rowA.ballsAgainst + match.teamBBalls
            )

            rowMap[teamB] = rowB.copy(
                played = playedB,
                won = wonB,
                draw = drawB,
                lost = lostB,
                points = pointsB,
                runsFor = rowB.runsFor + match.teamBScore,
                ballsFor = rowB.ballsFor + match.teamBBalls,
                runsAgainst = rowB.runsAgainst + match.teamAScore,
                ballsAgainst = rowB.ballsAgainst + match.teamABalls
            )
        }

        val sorted = rowMap.values.sortedWith(
            compareByDescending<TeamPointsRow> { it.points }
                .thenByDescending { it.netRunRate }
                .thenByDescending { it.scoreDifference }
                .thenByDescending { it.averageRunsPerMatch }
        )

        return sorted.mapIndexed { idx, row -> row.copy(position = idx + 1) }
    }

    suspend fun getPlayerStats(playerNameFilter: String? = null, tournamentIdFilter: Long? = null): List<PlayerStats> {
        val players = dao.getAllPlayersSync()
        val balls = dao.getAllBallsSync()
        val allMatches = if (tournamentIdFilter != null) {
            dao.getMatchesForTournamentSync(tournamentIdFilter)
        } else {
            val allTournamentsList = dao.getAllTournaments()
            // We can query all matches across all tournaments
            val matchesList = mutableListOf<MatchEntity>()
            // Query per tournament or get all
            val tourns = dao.getAllTournaments()
            // Let's gather all matches
            matchesList
        }

        val relevantMatches = if (tournamentIdFilter != null) {
            dao.getMatchesForTournamentSync(tournamentIdFilter)
        } else {
            // All matches
            val matches = mutableListOf<MatchEntity>()
            // Query all tournaments
            val tourns = dao.getAllTournaments().firstOrNull() ?: emptyList()
            for (t in tourns) {
                matches.addAll(dao.getMatchesForTournamentSync(t.id))
            }
            matches
        }

        val matchIds = relevantMatches.map { it.id }.toSet()
        val filteredBalls = balls.filter { matchIds.contains(it.matchId) }

        val targetPlayers = if (!playerNameFilter.isNullOrBlank()) {
            players.filter { it.name.contains(playerNameFilter, ignoreCase = true) }
        } else {
            players
        }

        val playerStatsList = mutableListOf<PlayerStats>()

        for (player in targetPlayers) {
            val name = player.name
            val playerBallsBatted = filteredBalls.filter { it.batsmanName.equals(name, ignoreCase = true) }
            val playerBallsBowled = filteredBalls.filter { it.bowlerName.equals(name, ignoreCase = true) }

            val runsScored = playerBallsBatted.sumOf { it.runs }
            val ballsFaced = playerBallsBatted.count { it.extraType != "WIDE" }
            val fours = playerBallsBatted.count { it.runs == 4 }
            val sixes = playerBallsBatted.count { it.runs == 6 }

            // Group batting per match
            val battingPerMatch = playerBallsBatted.groupBy { it.matchId }
            val inningsBatted = battingPerMatch.size
            var highestScore = 0
            var notOuts = 0

            for ((matchId, matchBalls) in battingPerMatch) {
                val matchRuns = matchBalls.sumOf { it.runs }
                if (matchRuns > highestScore) highestScore = matchRuns

                val gotOut = matchBalls.any { it.isWicket && it.dismissedPlayer.equals(name, ignoreCase = true) }
                if (!gotOut) notOuts++
            }

            // Bowling stats
            val wickets = playerBallsBowled.count { it.isWicket && it.dismissalType != "RUN_OUT" }
            val ballsBowledCount = playerBallsBowled.count { it.extraType != "WIDE" && it.extraType != "NO_BALL" }
            val runsConceded = playerBallsBowled.sumOf { it.runs }

            var bestWkts = 0
            var bestRuns = 999
            val bowlingPerMatch = playerBallsBowled.groupBy { it.matchId }
            for ((_, matchBalls) in bowlingPerMatch) {
                val matchWkts = matchBalls.count { it.isWicket && it.dismissalType != "RUN_OUT" }
                val matchRunsConceded = matchBalls.sumOf { it.runs }
                if (matchWkts > bestWkts || (matchWkts == bestWkts && matchRunsConceded < bestRuns && matchWkts > 0)) {
                    bestWkts = matchWkts
                    bestRuns = matchRunsConceded
                }
            }
            if (bestRuns == 999) bestRuns = 0

            // Matches played
            val matchesPlayed = relevantMatches.count { match ->
                val lineups = dao.getLineupForMatchSync(match.id)
                lineups.any { it.playerName.equals(name, ignoreCase = true) } ||
                        battingPerMatch.containsKey(match.id) ||
                        bowlingPerMatch.containsKey(match.id)
            }

            // Tournament history breakdown
            val tourns = dao.getAllTournaments().firstOrNull() ?: emptyList()
            val tournHistoryList = mutableListOf<TournamentPlayerStat>()
            for (t in tourns) {
                val tMatches = dao.getMatchesForTournamentSync(t.id).map { it.id }.toSet()
                val tBallsBatted = balls.filter { tMatches.contains(it.matchId) && it.batsmanName.equals(name, ignoreCase = true) }
                val tBallsBowled = balls.filter { tMatches.contains(it.matchId) && it.bowlerName.equals(name, ignoreCase = true) }
                if (tBallsBatted.isNotEmpty() || tBallsBowled.isNotEmpty()) {
                    val tRuns = tBallsBatted.sumOf { it.runs }
                    val tWkts = tBallsBowled.count { it.isWicket && it.dismissalType != "RUN_OUT" }
                    val tBatGroup = tBallsBatted.groupBy { it.matchId }
                    var tHs = 0
                    var tOuts = 0
                    for ((_, mBalls) in tBatGroup) {
                        val r = mBalls.sumOf { it.runs }
                        if (r > tHs) tHs = r
                        if (mBalls.any { it.isWicket && it.dismissedPlayer.equals(name, ignoreCase = true) }) tOuts++
                    }
                    val tAvg = if (tOuts > 0) tRuns.toDouble() / tOuts else tRuns.toDouble()
                    tournHistoryList.add(
                        TournamentPlayerStat(
                            tournamentName = t.name,
                            matches = tBatGroup.size.coerceAtLeast(1),
                            runs = tRuns,
                            highestScore = tHs,
                            average = tAvg,
                            wickets = tWkts
                        )
                    )
                }
            }

            playerStatsList.add(
                PlayerStats(
                    playerName = name,
                    teamName = player.teamName,
                    matchesPlayed = matchesPlayed.coerceAtLeast(inningsBatted),
                    inningsBatted = inningsBatted,
                    runsScored = runsScored,
                    ballsFaced = ballsFaced,
                    highestScore = highestScore,
                    notOuts = notOuts,
                    fours = fours,
                    sixes = sixes,
                    wicketsTaken = wickets,
                    ballsBowled = ballsBowledCount,
                    runsConceded = runsConceded,
                    bestBowlingWickets = bestWkts,
                    bestBowlingRuns = bestRuns,
                    tournamentHistory = tournHistoryList
                )
            )
        }

        return playerStatsList
    }

    suspend fun exportDataToJson(): String {
        val tourns = dao.getAllTournaments().firstOrNull() ?: emptyList()
        val activeT = dao.getActiveTournamentSync()
        val allMatches = mutableListOf<MatchEntity>()
        val allTeams = mutableListOf<TeamEntity>()
        val allLineups = mutableListOf<MatchPlayerEntity>()

        for (t in tourns) {
            allMatches.addAll(dao.getMatchesForTournamentSync(t.id))
            allTeams.addAll(dao.getTeamsForTournamentSync(t.id))
        }

        for (m in allMatches) {
            allLineups.addAll(dao.getLineupForMatchSync(m.id))
        }

        val allPlayers = dao.getAllPlayersSync()
        val allBalls = dao.getAllBallsSync()

        val root = JSONObject()
        root.put("app", "Head & Tail Tournament Manager")
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())

        val tournsArray = JSONArray()
        tourns.forEach { t ->
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("name", t.name)
            obj.put("startDate", t.startDate)
            obj.put("endDate", t.endDate ?: JSONObject.NULL)
            obj.put("format", t.format)
            obj.put("status", t.status)
            obj.put("championTeam", t.championTeam ?: JSONObject.NULL)
            obj.put("runnerUpTeam", t.runnerUpTeam ?: JSONObject.NULL)
            tournsArray.put(obj)
        }
        root.put("tournaments", tournsArray)

        val playersArray = JSONArray()
        allPlayers.forEach { p ->
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            obj.put("teamName", p.teamName)
            playersArray.put(obj)
        }
        root.put("players", playersArray)

        return root.toString(2)
    }

    suspend fun resetAllTournamentsKeepingPlayerStats() {
        val players = dao.getAllPlayersSync()
        dao.deleteAllBalls()
        dao.deleteAllMatches()
        dao.deleteAllTeams()
        dao.deleteAllTournaments()

        // Re-insert players so lifetime stats context or profiles remain available
        dao.insertPlayers(players)

        // Seed new default tournament
        initializeDefaultDataIfNeeded()
    }
}

private fun String?.isNullAndBlank(): Boolean = this == null || this.isBlank()
