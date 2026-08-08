package com.example.model

data class TeamPointsRow(
    val position: Int = 0,
    val teamName: String,
    val played: Int = 0,
    val won: Int = 0,
    val draw: Int = 0,
    val lost: Int = 0,
    val points: Int = 0,
    val runsFor: Int = 0,
    val ballsFor: Int = 0,
    val runsAgainst: Int = 0,
    val ballsAgainst: Int = 0
) {
    val oversFor: Double
        get() = ballsFor / 6 + (ballsFor % 6) * 0.1

    val oversAgainst: Double
        get() = ballsAgainst / 6 + (ballsAgainst % 6) * 0.1

    val runRateFor: Double
        get() {
            if (ballsFor == 0) return 0.0
            return (runsFor.toDouble() / ballsFor) * 6.0
        }

    val runRateAgainst: Double
        get() {
            if (ballsAgainst == 0) return 0.0
            return (runsAgainst.toDouble() / ballsAgainst) * 6.0
        }

    val netRunRate: Double
        get() = runRateFor - runRateAgainst

    val scoreDifference: Int
        get() = runsFor - runsAgainst

    val averageRunsPerMatch: Double
        get() {
            if (played == 0) return 0.0
            return runsFor.toDouble() / played
        }
}
