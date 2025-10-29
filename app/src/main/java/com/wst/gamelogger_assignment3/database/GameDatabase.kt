package com.wst.gamelogger_assignment3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wst.gamelogger_assignment3.Game
import com.wst.gamelogger_assignment3.database.Converters

@Database(entities = [Game::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase =
            INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_db"
                )
                    .fallbackToDestructiveMigration() // safe for dev; change as needed
                    .build()
                INSTANCE = inst
                inst
            }
    }
}