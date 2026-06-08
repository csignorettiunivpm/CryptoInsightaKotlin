package com.example.cryptoinsighta

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cryptoinsighta.ui.screen.CreaMainScreen
import com.example.cryptoinsighta.ui.screen.PortafoglioScreen.CreaPortafoglioScreen
import com.example.cryptoinsighta.ui.theme.CryptoInsightaTheme
import com.example.cryptoinsighta.worker.PriceAlertWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoInsightaTheme {
                CreaMainScreen()
            }
        }
        avviaControlloPrezziBackground()
    }

    private fun avviaControlloPrezziBackground() {
        val vincoli = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val alertCheckRequest = PeriodicWorkRequestBuilder<PriceAlertWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(vincoli).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "PriceAlertWork",
            ExistingPeriodicWorkPolicy.KEEP, // quest è pattern Singleton !! Se è già attivo, non lo resetta e non lo duplica
            alertCheckRequest
        )
    }
}


