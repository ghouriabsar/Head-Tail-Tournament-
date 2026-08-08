package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val format: String = "DOUBLE_ROUND_ROBIN",
    val status: String = "IN_PROGRESS", // IN_PROGRESS, COMPLETED
    val championTeam: String? = null,
    val runnerUpTeam: String? = null
)
