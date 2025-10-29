package com.wst.gamelogger_assignment3

import androidx.room.*
import com.wst.gamelogger_assignment3.database.Converters

@Entity(tableName = "game_table")
@TypeConverters(Converters::class)
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val platform: String,
    val genre: String = "",
    val overview: String? = null,
    val imageUrl: String? = null,
    val achievements: List<Achievement> = emptyList(),
    val notes: String? = null,
    val completed: Boolean = false                    // ⬅️ NEW
)

data class Achievement(
    val name: String,
    var completed: Boolean = false
)