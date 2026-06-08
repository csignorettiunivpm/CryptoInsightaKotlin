package com.example.cryptoinsighta.worker

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cryptoinsighta.api.RetrofitInstance
import com.example.cryptoinsighta.api.YahooFinanceService
import com.example.cryptoinsighta.dao.PriceAlertDao
import com.example.cryptoinsighta.database.AppDatabase
import com.example.cryptoinsighta.helper.NotificationHelper
import com.example.cryptoinsighta.model.PriceAlertWithTicker

class PriceAlertWorker(context: Context, workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {
    private val database = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, // <-- Cambia "AppDatabase" con il nome della tua classe del Database
        "cryptoinsighta_db" // <-- Metti il nome del file del tuo database (es. "crypto_database")
    ).build()

    private val priceAlertDao = database.priceAlertDao()

    private val yahooFinanceService = YahooFinanceService()
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork():Result{
        try{
            Log.d("appl","Worker chiamato!!")
            val alertsAttivi: List<PriceAlertWithTicker> = priceAlertDao.getActiveAlerts()
            alertsAttivi.forEach { alert ->
                val timestampMsIeri = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
                val ultimiPrezziAttuali = yahooFinanceService.ottieniPrezziAsset(alert.tickerAsset,timestampMsIeri)
                Log.d("appl","${ultimiPrezziAttuali}")
                val prezzoAttuale = ultimiPrezziAttuali.last().close
                Log.d("appl","${prezzoAttuale}")
                if(alert.priceAlert.PRAL_tipoSoglia == "Aumenta" && prezzoAttuale >= alert.priceAlert.PRAL_prezzoSoglia){
                    NotificationHelper.sendAlertNotification(applicationContext, alert, prezzoAttuale)
                    priceAlertDao.disattivaAvviso(alert.priceAlert.PRAL_Id)
                }else if (alert.priceAlert.PRAL_tipoSoglia == "Diminuisce" && prezzoAttuale <= alert.priceAlert.PRAL_prezzoSoglia){
                    NotificationHelper.sendAlertNotification(applicationContext, alert, prezzoAttuale)
                    priceAlertDao.disattivaAvviso(alert.priceAlert.PRAL_Id)
                }
            }
            return Result.success()
        }catch (e: Exception)
        {
            println(e)
            return Result.retry()
        }

    }
}