package com.wst.gamelogger_assignment3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wst.gamelogger_assignment3.Game
import com.wst.gamelogger_assignment3.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val repo: GameRepository) : ViewModel() {

    private val _allGames = MutableStateFlow<List<Game>>(emptyList())
    val allGames: StateFlow<List<Game>> = _allGames

    init {
        viewModelScope.launch {
            repo.allGames.collect { list -> _allGames.value = list }
        }
    }

    fun insertGame(game: Game) = viewModelScope.launch { repo.insert(game) }
    fun updateGame(game: Game) = viewModelScope.launch { repo.update(game) }
    fun deleteGame(game: Game) = viewModelScope.launch { repo.delete(game) }
    suspend fun getById(id: Int): Game? = repo.getById(id)
}