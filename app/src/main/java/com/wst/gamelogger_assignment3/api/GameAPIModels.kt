package com.wst.gamelogger_assignment3.api

data class GameSearchResponse(
    val results: List<GameResult>
)

data class GameResult(
    val name: String,
    val background_image: String?,
    val genres: List<Genre>,
    val description: String? = null
)

data class Genre(val name: String)