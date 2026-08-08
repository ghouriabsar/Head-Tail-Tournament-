package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.*
import com.example.data.repository.TournamentRepository
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TournamentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TournamentRepository

    val activeTournament: StateFlow<TournamentEntity?>
    val allTournaments: StateFlow<List<TournamentEntity>>
    val allPlayers: StateFlow<List<PlayerEntity>>

    private val _selectedTournamentId = MutableStateFlow<Long?>(null)
    val selectedTournamentId: StateFlow<Long?> = _selectedTournamentId.asStateFlow()

    private val _matches = MutableStateFlow<List<MatchEntity>>(emptyList())
    val matches: StateFlow<List<MatchEntity>> = _matches.asStateFlow()

    private val _teams = MutableStateFlow<List<TeamEntity>>(emptyList())
    val teams: StateFlow<List<TeamEntity>> = _teams.asStateFlow()

    private val _pointsTable = MutableStateFlow<List<TeamPointsRow>>(emptyList())
    val pointsTable: StateFlow<List<TeamPointsRow>> = _pointsTable.asStateFlow()

    private val _selectedMatchId = MutableStateFlow<Long?>(null)
    val selectedMatchId: StateFlow<Long?> = _selectedMatchId.asStateFlow()

    private val _selectedMatch = MutableStateFlow<MatchEntity?>(null)
    val selectedMatch: StateFlow<MatchEntity?> = _selectedMatch.asStateFlow()

    private val _ballsForSelectedMatch = MutableStateFlow<List<BallEntity>>(emptyList())
    val ballsForSelectedMatch: StateFlow<List<BallEntity>> = _ballsForSelectedMatch.asStateFlow()

    private val _lineupForSelectedMatch = MutableStateFlow<List<MatchPlayerEntity>>(emptyList())
    val lineupForSelectedMatch: StateFlow<List<MatchPlayerEntity>> = _lineupForSelectedMatch.asStateFlow()

    private val _playerStatsList = MutableStateFlow<List<PlayerStats>>(emptyList())
    val playerStatsList: StateFlow<List<PlayerStats>> = _playerStatsList.asStateFlow()

    // Animation Event Trigger State
    private val _animationEvent = MutableStateFlow<AnimationEvent?>(null)
    val animationEvent: StateFlow<AnimationEvent?> = _animationEvent.asStateFlow()

    init {
        val database = com.example.data.db.AppDatabase.getInstance(application)
        repository = TournamentRepository(database.tournamentDao())

        activeTournament = repository.activeTournament.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        allTournaments = repository.allTournaments.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allPlayers = repository.allPlayers.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.initializeDefaultDataIfNeeded()
        }

        // Observe active tournament to update sub-flows
        viewModelScope.launch {
            activeTournament.collect { tournament ->
                if (tournament != null) {
                    _selectedTournamentId.value = tournament.id
                    observeTournamentData(tournament.id)
                }
            }
        }
    }

    fun selectTournament(tournamentId: Long) {
        _selectedTournamentId.value = tournamentId
        observeTournamentData(tournamentId)
    }

    private fun observeTournamentData(tournamentId: Long) {
        viewModelScope.launch {
            repository.getMatchesForTournament(tournamentId).collect {
                _matches.value = it
            }
        }
        viewModelScope.launch {
            repository.getTeamsForTournament(tournamentId).collect {
                _teams.value = it
            }
        }
        viewModelScope.launch {
            repository.getPointsTable(tournamentId).collect {
                _pointsTable.value = it
            }
        }
        refreshPlayerStats()
    }

    fun selectMatch(matchId: Long) {
        _selectedMatchId.value = matchId
        viewModelScope.launch {
            repository.getMatchById(matchId).collect {
                _selectedMatch.value = it
            }
        }
        viewModelScope.launch {
            repository.getBallsForMatch(matchId).collect {
                _ballsForSelectedMatch.value = it
            }
        }
        viewModelScope.launch {
            repository.getLineupForMatch(matchId).collect {
                _lineupForSelectedMatch.value = it
            }
        }
    }

    fun updateTossAndLineup(
        matchId: Long,
        tossWinner: String,
        tossDecision: String,
        teamAPlayers: List<String>,
        teamBPlayers: List<String>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTossAndLineup(matchId, tossWinner, tossDecision, teamAPlayers, teamBPlayers)
            selectMatch(matchId)
        }
    }

    fun startSecondInnings(matchId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.startSecondInnings(matchId)
            selectMatch(matchId)
        }
    }

    fun recordBall(
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
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val ball = repository.recordBall(
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

            // Trigger Animation Event
            if (isWicket) {
                _animationEvent.value = AnimationEvent.Wicket
            } else if (runs == 4) {
                _animationEvent.value = AnimationEvent.Four
            } else if (runs == 6) {
                _animationEvent.value = AnimationEvent.Six
            }

            refreshPlayerStats()
        }
    }

    fun clearAnimationEvent() {
        _animationEvent.value = null
    }

    fun undoLastBall(matchId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.undoLastBall(matchId)
            refreshPlayerStats()
        }
    }

    fun deleteBall(ballId: Long, matchId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBall(ballId, matchId)
            refreshPlayerStats()
        }
    }

    fun resolveDrawWithPoints(matchId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resolveDrawWithPoints(matchId)
        }
    }

    fun completeSuperOver(matchId: Long, winnerTeamName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.completeSuperOver(matchId, winnerTeamName)
        }
    }

    fun createNewTournament(name: String, teamList: List<Pair<String, String>>) {
        viewModelScope.launch(Dispatchers.IO) {
            val newId = repository.createNewTournament(name, teamList)
            selectTournament(newId)
        }
    }

    fun refreshPlayerStats(searchQuery: String? = null, tournamentIdFilter: Long? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val stats = repository.getPlayerStats(searchQuery, tournamentIdFilter)
            _playerStatsList.value = stats
        }
    }

    fun resetTournamentKeepingPlayerStats() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetAllTournamentsKeepingPlayerStats()
        }
    }

    suspend fun exportDataJson(): String {
        return repository.exportDataToJson()
    }
}

sealed class AnimationEvent {
    object Four : AnimationEvent()
    object Six : AnimationEvent()
    object Wicket : AnimationEvent()
}
