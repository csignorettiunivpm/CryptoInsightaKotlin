package com.example.cryptoinsighta.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cryptoinsighta.dao.AssetDao
import com.example.cryptoinsighta.dao.PriceAlertDao
import com.example.cryptoinsighta.dao.PriceHistoryDao
import com.example.cryptoinsighta.dao.TransactionDao
import com.example.cryptoinsighta.model.Asset
import com.example.cryptoinsighta.model.PriceAlert
import com.example.cryptoinsighta.model.PriceHistory
import com.example.cryptoinsighta.model.Transaction

@Database(
    entities = [
        Asset::class,
        Transaction::class,
        PriceHistory::class,
        PriceAlert::class
    ],
    version = 3, //versione del db, se volessi cambiare struttura, devo incrementarla
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    //espongo i dao
    abstract fun assetDao(): AssetDao
    abstract fun transactionDao(): TransactionDao
    abstract fun priceHistoryDao(): PriceHistoryDao
    abstract fun priceAlertDao(): PriceAlertDao

    //companion object, mi fa da pattern singleton, ossia mi garantisce che ci sia una sola istanza
    //del db ad ogni esecuzione dell'app
    companion object {
        @Volatile       //volatile fa si che INSTANCE (l'istanza del DB) sia sempre aggiornata tra i thread
        private var INSTANCE: AppDatabase? = null


        //context è un oggetto sempre disponibile, mi dice dove mi trovo
        fun getInstance(context: Context): AppDatabase {
            //QUI C'è ELVIS (?:) serve appunto per dire, che se INSTANCE è NON è null,
            //allora restituisci l'istanza del db attuale alla riga di codice che l'ha chiesta
            //ossia se non è null ritorna INSTANCE, altrimenti fai quello dopo ELVIS

            //synchronized(this) impedisce che due thread possano richiedere il db insieme, creando di fatto due copie della stessa istanza
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cryptoinsighta_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
                //potevo anche fare solo instance, ma return instance per ora mi è più chiaro
            }
        }
    }
}