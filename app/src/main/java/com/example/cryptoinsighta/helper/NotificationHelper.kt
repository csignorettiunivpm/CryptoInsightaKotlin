package com.example.cryptoinsighta.helper


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.cryptoinsighta.R
import com.example.cryptoinsighta.model.PriceAlert
import com.example.cryptoinsighta.model.PriceAlertWithTicker

object NotificationHelper {
    private const val CHANNEL_ID = "investment_alerts_channel"
    fun sendAlertNotification(context: Context, alert: PriceAlertWithTicker, currentPrice: Double){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Avvisi Prezzi CryptoInsighta",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val titoloNotifica = "Soglia di prezzo raggiunta!"
        val messaggio = "L'asset ${alert.tickerAsset} ha raggiunto € ${currentPrice} (Soglia impostata: € ${alert.priceAlert.PRAL_prezzoSoglia})"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titoloNotifica)
            .setContentText(messaggio)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(alert.priceAlert.PRAL_Id.hashCode(), builder.build())
    }
}