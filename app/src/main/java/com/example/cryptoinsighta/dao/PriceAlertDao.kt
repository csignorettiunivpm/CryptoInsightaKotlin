package com.example.cryptoinsighta.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cryptoinsighta.model.PriceAlert
import com.example.cryptoinsighta.model.PriceAlertWithTicker
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceAlertDao {

    @Query("SELECT * FROM price_alerts WHERE PRAS_Id = :assetId")
    suspend fun getAlertsByAsset(assetId: Int): List<PriceAlert>

    @Query("SELECT p.*, a.ASSE_ticker FROM price_alerts AS p INNER JOIN assets a ON PRAS_Id = ASSE_Id WHERE PRAL_isAttivo = 1")
    suspend fun getActiveAlerts(): List<PriceAlertWithTicker>  // usato dal WorkManager

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlert)

    @Update
    suspend fun updateAlert(alert: PriceAlert)

    @Delete
    suspend fun deleteAlert(alert: PriceAlert)
    @Query("UPDATE price_alerts SET PRAL_isAttivo = 0 WHERE PRAL_Id = :pralId")
    suspend fun disattivaAvviso(pralId: Int)
    @Query("DELETE FROM price_alerts")
    suspend fun svuotaTabella()
}