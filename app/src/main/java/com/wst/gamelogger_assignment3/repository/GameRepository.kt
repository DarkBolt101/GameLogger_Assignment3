package com.wst.gamelogger_assignment3.repository

import com.wst.gamelogger_assignment3.Game
import com.wst.gamelogger_assignment3.data.GameDao
import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: GameDao) {

    val activeGames: Flow<List<Game>> = dao.getActiveGames()
    val completedGames: Flow<List<Game>> = dao.getCompletedGames()

    suspend fun insertGame(game: Game) = dao.insertGame(game)
    suspend fun updateGame(game: Game) = dao.updateGame(game)
    suspend fun deleteGame(game: Game) = dao.deleteGame(game)
    suspend fun setCompleted(id: Int, isCompleted: Boolean) = dao.updateCompleted(id, isCompleted)
}