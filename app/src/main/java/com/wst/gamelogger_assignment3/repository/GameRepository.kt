package com.wst.gamelogger_assignment3.repository

import com.wst.gamelogger_assignment3.Game
import com.wst.gamelogger_assignment3.data.GameDao
import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: GameDao) {
    val allGames: Flow<List<Game>> = dao.getAll()
    suspend fun insert(game: Game) = dao.insert(game)
    suspend fun update(game: Game) = dao.update(game)
    suspend fun delete(game: Game) = dao.delete(game)
    suspend fun getById(id: Int): Game? = dao.getById(id)
}