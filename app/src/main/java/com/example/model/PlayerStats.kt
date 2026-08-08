package com.example.model

data class PlayerStats(
    val playerName: String,
    val teamName: String = "",
    val matchesPlayed: Int = 0,
    val inningsBatted: Int = 0,
    val runsScored: Int = 0,
    val ballsFaced: Int = 0,
    val highestScore: Int = 0,
    val notOuts: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val wicketsTaken: Int = 0,
    val ballsBowled: Int = 0,
    val runsConceded: Int = 0,
    val bestBowlingWickets: Int = 0,
    val bestBowlingRuns: Int = 0,
    val tournamentHistory: List<TournamentPlayerStat> = emptyList()
) {
    val battingAverage: Double
        get() {
            val outs = inningsBatted - notOuts
            if (outs <= 0) return if (runsScored > 0) runsScored.toDouble() else 0.0
            return runsScored.toDouble() / outs
        }

    val strikeRate: Double
        get() {
            if (ballsFaced == 0) return 0.0
            return (runsScored.toDouble() / ballsFaced) * 100.0
        }

    val bowlingAverage: Double
        get() {
            if (wicketsTaken == 0) return 0.0
            return runsConceded.toDouble() / wicketsTaken
        }

    val bowlingEconomy: Double
        get() {
            if (ballsBowled == 0) return 0.0
            val overs = ballsBowled / 6.0
            return runsConceded.toDouble() / overs
        }

    val bestBowlingString: String
        get() {
            if (bestBowlingWickets == 0 && bestBowlingRuns == 0) return "-"
            return "$bestBowlingWickets/$bestBowlingRuns"
        }
}

data class TournamentPlayerStat(
    val tournamentName: String,
    val matches: Int,
    val runs: Int,
    val highestScore: Int,
    val average: Double,
    val wickets: Int
)
