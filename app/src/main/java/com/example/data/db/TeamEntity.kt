package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tournamentId: Long,
    val name: String,
    val captainName: String = "",
    val colorHex: String = "#1E5128"
)
