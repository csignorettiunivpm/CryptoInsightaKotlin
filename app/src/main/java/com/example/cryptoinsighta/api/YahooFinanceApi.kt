package com.example.cryptoinsighta.api

import com.example.cryptoinsighta.model.modelsFinanceAPI.YahooFinanceResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface  YahooFinanceApi {
    @GET("v8/finance/chart/{ticker}")
    suspend fun getPrices(
        @Path("ticker") ticker: String,
        @Query("interval") interval: String = "1d",
        @Query("period1") period1: Long,
        @Query("period2") period2: Long
    ): YahooFinanceResponse

}