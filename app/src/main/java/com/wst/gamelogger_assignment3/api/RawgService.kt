package com.wst.gamelogger_assignment3.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RawgService {
    @GET("games")
    suspend fun searchGames(
        @Query("search") query: String,
        @Query("key") apiKey: String
    ): RawgSearchResponse

    @GET("games/{id}")
    suspend fun getGameDetails(
        @Path("id") idOrSlug: String,
        @Query("key") apiKey: String
    ): RawgGameDetails

    @GET("games/{id}/achievements")
    suspend fun getAchievements(
        @Path("id") idOrSlug: String,
        @Query("key") apiKey: String
    ): RawgAchievementResponse

    companion object {
        private const val BASE = "https://api.rawg.io/api/"
        fun create(): RawgService {
            val r = Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return r.create(RawgService::class.java)
        }
    }
}