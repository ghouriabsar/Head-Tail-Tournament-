package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {
    // Tournaments
    @Query("SELECT * FROM tournaments ORDER BY id DESC")
    fun getAllTournaments(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE id = :id")
    fun getTournamentById(id: Long): Flow<TournamentEntity?>

    @Query("SELECT * FROM tournaments WHERE status = 'IN_PROGRESS' ORDER BY id DESC LIMIT 1")
    fun getActiveTournament(): Flow<TournamentEntity?>

    @Query("SELECT * FROM tournaments WHERE status = 'IN_PROGRESS' ORDER BY id DESC LIMIT 1")
    suspend fun getActiveTournamentSync(): TournamentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity): Long

    @Update
    suspend fun updateTournament(tournament: TournamentEntity)

    // Teams
    @Query("SELECT * FROM teams WHERE tournamentId = :tournamentId")
    fun getTeamsForTournament(tournamentId: Long): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE tournamentId = :tournamentId")
    suspend fun getTeamsForTournamentSync(tournamentId: Long): List<TeamEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams: List<TeamEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity): Long

    @Query("DELETE FROM teams WHERE id = :id")
    suspend fun deleteTeam(id: Long)

    // Players
    @Query("SELECT * FROM players ORDER BY name ASC")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players ORDER BY name ASC")
    suspend fun getAllPlayersSync(): List<PlayerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Query("DELETE FROM players WHERE id = :id")
    suspend fun deletePlayer(id: Long)

    // Matches
    @Query("SELECT * FROM matches WHERE tournamentId = :tournamentId ORDER BY matchNumber ASC")
    fun getMatchesForTournament(tournamentId: Long): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE tournamentId = :tournamentId ORDER BY matchNumber ASC")
    suspend fun getMatchesForTournamentSync(tournamentId: Long): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE id = :matchId")
    fun getMatchById(matchId: Long): Flow<MatchEntity?>

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchByIdSync(matchId: Long): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity): Long

    @Update
    suspend fun updateMatch(match: MatchEntity)

    // Balls
    @Query("SELECT * FROM balls WHERE matchId = :matchId ORDER BY id ASC")
    fun getBallsForMatch(matchId: Long): Flow<List<BallEntity>>

    @Query("SELECT * FROM balls WHERE matchId = :matchId ORDER BY id ASC")
    suspend fun getBallsForMatchSync(matchId: Long): List<BallEntity>

    @Query("SELECT * FROM balls ORDER BY id ASC")
    suspend fun getAllBallsSync(): List<BallEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBall(ball: BallEntity): Long

    @Query("DELETE FROM balls WHERE id = :ballId")
    suspend fun deleteBall(ballId: Long)

    @Query("DELETE FROM balls WHERE matchId = :matchId")
    suspend fun deleteBallsForMatch(matchId: Long)

    @Update
    suspend fun updateBall(ball: BallEntity)

    // Match Players (Lineup)
    @Query("SELECT * FROM match_players WHERE matchId = :matchId")
    fun getLineupForMatch(matchId: Long): Flow<List<MatchPlayerEntity>>

    @Query("SELECT * FROM match_players WHERE matchId = :matchId")
    suspend fun getLineupForMatchSync(matchId: Long): List<MatchPlayerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchPlayers(lineup: List<MatchPlayerEntity>)

    @Query("DELETE FROM match_players WHERE matchId = :matchId")
    suspend fun deleteLineupForMatch(matchId: Long)

    // Reset Database (Safely clear all tournaments or clear everything)
    @Query("DELETE FROM balls")
    suspend fun deleteAllBalls()

    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()

    @Query("DELETE FROM teams")
    suspend fun deleteAllTeams()

    @Query("DELETE FROM tournaments")
    suspend fun deleteAllTournaments()

    @Query("DELETE FROM players")
    suspend fun deleteAllPlayers()
}
