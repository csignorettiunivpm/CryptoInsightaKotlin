package com.example.cryptoinsighta.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cryptoinsighta.model.Transaction
import com.example.cryptoinsighta.model.TransactionWithTicker
import kotlinx.coroutines.flow.Flow
import androidx.room.Transaction as RoomTransaction
@Dao
interface TransactionDao {


    @Query("SELECT * FROM transactions WHERE TRAS_Id = :assetId ORDER BY TRAN_dataOra DESC")
    suspend fun getTransactionsByAsset(assetId: Int): List<Transaction>

    @Query("SELECT * FROM transactions ORDER BY TRAN_dataOra ASC")
    suspend fun getAllTransactions(): List<Transaction>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
    @RoomTransaction
    suspend fun deleteTransactionEAggiornaAsset(transaction: Transaction){
        deleteTransaction(transaction)
        val variazione = if (transaction.TRAN_tipo == "BUY") {
            // se l'utente cancella un BUY allora gli devo togliere la qta che aveva comprato ma adesso ha cancellato
            -transaction.TRAN_qta
        } else {
            // se l'utente cancella un SELL allora gli devo ridare la qta che aveva venduto ma adesso ha cancellato
            +transaction.TRAN_qta
        }
        aggiornaQuantitaAsset(transaction.TRAS_Id, variazione)

    }
    @Query("DELETE FROM transactions ")
    suspend fun svuotaTabella()
    @Query("UPDATE assets SET ASSE_qtaPosseduta = ASSE_qtaPosseduta + :variazione WHERE ASSE_Id = :assetId")
    suspend fun aggiornaQuantitaAsset(assetId: Int, variazione: Double)

    @RoomTransaction
    suspend fun inserisciTransazioneEAgiornaAsset(transaction: Transaction, qtaPossedutaAsset: Double) {
        insertTransaction(transaction)

        // Calcola se sommare o sottrarre in base al tipo
        val variazione = if (transaction.TRAN_tipo == "BUY") {
            transaction.TRAN_qta
        } else {
            if(qtaPossedutaAsset - transaction.TRAN_qta < 0) {
                0.0
            }else
            {
                -transaction.TRAN_qta
            }

        }


        aggiornaQuantitaAsset(transaction.TRAS_Id, variazione)
    }

    @Query("""
    SELECT t.*, a.ASSE_ticker AS tickerAsset 
    FROM transactions t 
    INNER JOIN assets a ON t.TRAS_Id = a.ASSE_Id
    ORDER BY t.TRAN_dataOra DESC
""")
    suspend fun getAllTransactionsWithTicker(): List<TransactionWithTicker>

}