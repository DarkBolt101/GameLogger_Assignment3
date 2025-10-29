package com.wst.gamelogger_assignment3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wst.gamelogger_assignment3.Game
import com.wst.gamelogger_assignment3.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(private val repo: GameRepository) : ViewModel() {

    val activeGames = repo.activeGames.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val completedGames = repo.completedGames.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertGame(g: Game) = viewModelScope.launch { repo.insertGame(g) }
    fun updateGame(g: Game) = viewModelScope.launch { repo.updateGame(g) }
    fun deleteGame(g: Game) = viewModelScope.launch { repo.deleteGame(g) }

    fun toggleCompleted(game: Game, isCompleted: Boolean) = viewModelScope.launch {
        repo.setCompleted(game.id, isCompleted)
    }
}