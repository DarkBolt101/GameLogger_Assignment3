package com.wst.gamelogger_assignment3

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wst.gamelogger_assignment3.database.Converters

@Entity(tableName = "games")
@TypeConverters(Converters::class)
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val platform: String,
    val genre: String = "",
    val imageUrl: String? = null,
    val overview: String? = null,
    val achievements: List<Achievement> = emptyList(),
    val notes: String? = null
)

data class Achievement(
    val name: String,
    var completed: Boolean = false
)