package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tournamentId: Long,
    val matchNumber: Int,
    val teamA: String,
    val teamB: String,
    val isFinal: Boolean = false,
    val status: String = "SCHEDULED", // SCHEDULED, LIVE, COMPLETED
    val tossWinner: String? = null,
    val tossDecision: String? = null, // BAT, BOWL
    val winnerTeam: String? = null,
    val isDraw: Boolean = false,
    val isSuperOver: Boolean = false,
    val superOverWinner: String? = null,
    val winningMargin: String? = null,
    val teamAScore: Int = 0,
    val teamAWickets: Int = 0,
    val teamABalls: Int = 0,
    val teamBScore: Int = 0,
    val teamBWickets: Int = 0,
    val teamBBalls: Int = 0,
    val oversPerInnings: Int = 6,
    val currentInnings: Int = 1, // 1 = Innings 1, 2 = Innings 2, 3 = Super Over 1, 4 = Super Over 2
    val timestamp: Long = System.currentTimeMillis()
)
