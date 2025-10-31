package com.wst.gamelogger_assignment3.data

import androidx.room.*
import com.wst.gamelogger_assignment3.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM game_table WHERE completed = 0 ORDER BY title ASC")
    fun getActiveGames(): Flow<List<Game>>

    @Query("SELECT * FROM game_table WHERE completed = 1 ORDER BY title ASC")
    fun getCompletedGames(): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("UPDATE game_table SET completed = :isCompleted WHERE id = :gameId")
    suspend fun updateCompleted(gameId: Int, isCompleted: Boolean)
}