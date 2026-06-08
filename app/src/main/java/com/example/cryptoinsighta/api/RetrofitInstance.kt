package com.example.cryptoinsighta.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.jvm.java

//configura la connessione a internet
object RetrofitInstance {
    //prende tutto quello che viaggia su Internet (la richiesta inviata e la risposta JSON che arriva da Yahoo Finance) e lo stampa testualmente dentro il Logcat di Android Studio.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // mostra le chiamate nel Logcat
    }

    //Retrofit da solo non sa come mandare pacchetti di dati su Internet. Si appoggia a una libreria sottostante chiamata OkHttpClient, che si comporta come un vero e proprio postino
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: YahooFinanceApi by lazy { //"lazy crealo solo quando serve"
        Retrofit.Builder()
            .baseUrl("https://query1.finance.yahoo.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Questo convertitore prende il JSON restituito dalle appi e, basandosi sulle mie data class (YahooFinanceResponse, Chart, ecc.), lo trasforma automaticamente in oggetti Kotlin pronti all'uso,
            .build()
            .create(YahooFinanceApi::class.java)
    }
}