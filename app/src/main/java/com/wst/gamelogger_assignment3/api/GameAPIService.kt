package com.wst.gamelogger_assignment3.api

import retrofit2.http.GET
import retrofit2.http.Query

interface GameAPIService {
    @GET("games")
    suspend fun searchGames(
        @Query("search") title: String,
        @Query("key") apiKey: String
    ): GameSearchResponse
}