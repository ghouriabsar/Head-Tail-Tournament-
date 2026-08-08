package com.example.model

import com.example.data.db.BallEntity
import com.example.data.db.MatchEntity

data class MatchDetail(
    val match: MatchEntity,
    val balls: List<BallEntity> = emptyList(),
    val teamAPlayers: List<String> = emptyList(),
    val teamBPlayers: List<String> = emptyList()
)

data class TournamentDashboardSummary(
    val tournamentName: String,
    val currentLeader: String,
    val totalTeams: Int,
    val totalMatches: Int,
    val completedMatches: Int,
    val remainingMatches: Int,
    val progressPercentage: Float,
    val champion: String?,
    val nextMatch: MatchEntity?
)

data class ExportBackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val tournaments: List<com.example.data.db.TournamentEntity>,
    val teams: List<com.example.data.db.TeamEntity>,
    val players: List<com.example.data.db.PlayerEntity>,
    val matches: List<com.example.data.db.MatchEntity>,
    val balls: List<com.example.data.db.BallEntity>,
    val matchPlayers: List<com.example.data.db.MatchPlayerEntity>
)
