package com.wst.gamelogger_assignment3.api

data class RawgSearchResponse(val results: List<RawgSearchItem>)
data class RawgSearchItem(
    val id: Int?,
    val slug: String?,
    val name: String?,
    val background_image: String?
)

data class RawgGameDetails(
    val id: Int?,
    val slug: String?,
    val name: String?,
    val description_raw: String?,
    val genres: List<RawgGenre>?,
    val background_image: String?
)

data class RawgGenre(val name: String)

data class RawgAchievementResponse(val results: List<RawgAchievementItem>)
data class RawgAchievementItem(val id: Int?, val name: String)