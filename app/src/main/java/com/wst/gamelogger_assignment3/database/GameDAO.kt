package com.wst.gamelogger_assignment3.data

import androidx.room.*
import com.wst.gamelogger_assignment3.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY title ASC")
    fun getAll(): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game): Long

    @Update
    suspend fun update(game: Game)

    @Delete
    suspend fun delete(game: Game)

    @Query("SELECT * FROM games WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Game?
}