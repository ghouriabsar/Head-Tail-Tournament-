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
import kotlinx.coroutines.withContext

class TournamentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TournamentRepository

    val activeTournament: StateFlow<TournamentEntity?>
    val allTournaments: StateFlow<List<TournamentEntity>>
    val allPlayers: StateFlow<List<PlayerEntity>>

    private val _selectedTournamentId = MutableStateFlow<Long?>(null)
    val selectedTournamentId: StateFlow<Long?> = _selectedTournamentId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val matches: StateFlow<List<MatchEntity>> = _selectedTournamentId
        .flatMapLatest { id ->
            if (id != null) repository.getMatchesForTournament(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val teams: StateFlow<List<TeamEntity>> = _selectedTournamentId
        .flatMapLatest { id ->
            if (id != null) repository.getTeamsForTournament(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val pointsTable: StateFlow<List<TeamPointsRow>> = _selectedTournamentId
        .flatMapLatest { id ->
            if (id != null) repository.getPointsTable(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedMatchId = MutableStateFlow<Long?>(null)
    val selectedMatchId: StateFlow<Long?> = _selectedMatchId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedMatch: StateFlow<MatchEntity?> = _selectedMatchId
        .flatMapLatest { id ->
            if (id != null) repository.getMatchById(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val ballsForSelectedMatch: StateFlow<List<BallEntity>> = _selectedMatchId
        .flatMapLatest { id ->
            if (id != null) repository.getBallsForMatch(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val lineupForSelectedMatch: StateFlow<List<MatchPlayerEntity>> = _selectedMatchId
        .flatMapLatest { id ->
            if (id != null) repository.getLineupForMatch(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

        // Observe active tournament to update selected ID
        viewModelScope.launch {
            activeTournament.collect { tournament ->
                if (tournament != null) {
                    _selectedTournamentId.value = tournament.id
                    refreshPlayerStats()
                }
            }
        }
    }

    fun selectTournament(tournamentId: Long) {
        _selectedTournamentId.value = tournamentId
        refreshPlayerStats()
    }

    fun selectMatch(matchId: Long) {
        _selectedMatchId.value = matchId
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

    fun startSecondInnings(matchId: Long, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.startSecondInnings(matchId)
            _selectedMatchId.value = matchId
            withContext(Dispatchers.Main) {
                onComplete?.invoke()
            }
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
