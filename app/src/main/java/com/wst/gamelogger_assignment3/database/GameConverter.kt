package com.wst.gamelogger_assignment3.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wst.gamelogger_assignment3.Achievement

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromAchievements(list: List<Achievement>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toAchievements(json: String?): List<Achievement> {
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<Achievement>>() {}.type
        return gson.fromJson(json, type)
    }
}