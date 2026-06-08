package com.example.cryptoinsighta.dao

import androidx.room.*
import com.example.cryptoinsighta.model.PriceHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceHistoryDao {



    @Query("SELECT * FROM price_history WHERE PRAS_Id = :assetId ORDER BY PRHI_dataOra ASC")
    suspend fun getPriceHistoryByAsset(assetId: Int): List<PriceHistory>

    @Query("SELECT * FROM price_history WHERE PRAS_Id = :assetId ORDER BY PRHI_dataOra DESC LIMIT 1")
    suspend fun getLastPrice(assetId: Int): PriceHistory?       //? significa che l'oggetto restituito può esse null

    @Query("""
        SELECT * FROM price_history 
        WHERE PRAS_Id = :assetId 
          AND PRHI_dataOra <= (:ultimoTimestamp - 86400000) 
        ORDER BY PRHI_dataOra DESC 
        LIMIT 1
    """)
    suspend fun getYesterdayPrice(assetId: Int, ultimoTimestamp: Long): PriceHistory?

    @Query("""
        SELECT * FROM price_history 
        WHERE PRAS_Id = :assetId AND PRHI_dataOra BETWEEN :daData AND :aData
        LIMIT 1
    """)//limit 1 perchè così escludo la casistica che possa restituire più di un prezzo in quella giornata, anche s enon dovrebbe
    suspend fun getPriceByDate(assetId: Int, daData:Long, aData:Long): PriceHistory
    @Query("SELECT * FROM price_history WHERE PRAS_Id = :assetId AND PRHI_dataOra >= :fromDate ORDER BY PRHI_dataOra ASC")
    suspend fun getPriceHistoryFromDate(assetId: Int, fromDate: Long): List<PriceHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrice(priceHistory: PriceHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrices(prices: List<PriceHistory>)  // inserisce lista intera

    @Query("DELETE FROM price_history")
    suspend fun svuotaTabella()
    @Query("""
        SELECT MIN(PRHI_dataOra) FROM price_history WHERE PRAS_Id = :assetId
    """)
    suspend fun getFirstAvailableTimestamp(assetId: Int):Long?
}