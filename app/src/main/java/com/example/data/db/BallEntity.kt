package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balls")
data class BallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matchId: Long,
    val innings: Int, // 1 or 2 (or 3/4 for super overs)
    val overNumber: Int, // 0-based index or 1-based index (e.g. over 0, 1, 2...)
    val ballNumber: Int, // 1 to 6
    val batsmanName: String,
    val nonStrikerName: String,
    val bowlerName: String,
    val battingTeam: String,
    val runs: Int, // 0, 1, 2, 3, 4, 5, 6
    val isWicket: Boolean = false,
    val dismissalType: String? = null, // BOWLED, CAUGHT, RUN_OUT, LBW, STUMPED
    val dismissedPlayer: String? = null,
    val extraType: String? = null, // WIDE, NO_BALL, BYE, LEG_BYE
    val timestamp: Long = System.currentTimeMillis()
)
