package com.example.cryptoinsighta

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cryptoinsighta.dao.AssetDao
import com.example.cryptoinsighta.dao.TransactionDao
import com.example.cryptoinsighta.database.AppDatabase
import com.example.cryptoinsighta.model.Asset
import com.example.cryptoinsighta.model.Transaction
import com.example.cryptoinsighta.repository.AssetRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db: AppDatabase

    private lateinit var assetDao: AssetDao
    private lateinit var transactionDao: TransactionDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Crea un database temporaneo in memoria isolato per i test
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        assetDao = db.assetDao()
        transactionDao = db.transactionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        // Chiude il database temporaneo liberando la memoria della RAM
        db.clearAllTables()
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadAsset() = runBlocking {
        val nuovoAsset = Asset(ASSE_nome = "Bitcoin", ASSE_ticker = "BTC-EUR", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.0, ASSE_categoria = "Crypto")

        assetDao.insertAsset(nuovoAsset)

        val listaAsset = assetDao.getAllAssets()

        assertTrue(listaAsset.any { it.ASSE_ticker == "BTC-EUR" })
        val assetLetto = listaAsset.first { it.ASSE_ticker == "BTC-EUR" }
        assertEquals(0.0, assetLetto.ASSE_qtaPosseduta, 0.0)
    }

    @Test
    fun testInserisciTransazioneEAggiornaAsset_Acquisto() = runBlocking {
        val assetIniziale = Asset(ASSE_nome = "Bitcoin", ASSE_ticker = "BTC-EUR", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.0, ASSE_categoria = "Crypto")
        val idAssetInserito = assetDao.insertAsset(assetIniziale)



        val transazioneBuy = Transaction(TRAS_Id = idAssetInserito.toInt(), TRAN_tipo = "BUY", TRAN_qta = 0.5, TRAN_prezzoUnitario = 10000.0, TRAN_dataOra = System.currentTimeMillis())


        transactionDao.inserisciTransazioneEAgiornaAsset(transazioneBuy, 0.0)


        val assetAggiornato = assetDao.getAssetById(idAssetInserito.toInt())
        assertEquals(0.5, assetAggiornato.ASSE_qtaPosseduta, 0.0)
    }

    @Test
    fun testInserisciTransazioneEAggiornaAsset_VenditaMaggioreDellaDisponibilita() = runBlocking {
        // Inseriamo un assetb quantità iniziale = 0.2
        val assetIniziale = Asset(ASSE_nome = "Ethereum", ASSE_ticker = "ETH-EUR", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.2, ASSE_categoria = "Crypto")
        val idAssetInserito = assetDao.insertAsset(assetIniziale)

        // Prepariamo una transazione di VENDITA (SELL) di 0.5 ETH (ne abbiamo solo 0.2!)
        val transazioneSellEccessiva = Transaction(TRAS_Id = idAssetInserito.toInt(), TRAN_tipo = "SELL", TRAN_qta = 0.5, TRAN_prezzoUnitario = 10000.0, TRAN_dataOra = System.currentTimeMillis())

        // la variazione deve essere 0.0
        transactionDao.inserisciTransazioneEAgiornaAsset(transazioneSellEccessiva, 0.2)

        // Verifichiamo che la quantità dell'asset NON sia andata in negativo e sia rimasta invariata (o aggiornata con +0.0)
        val assetDopoVendita = assetDao.getAssetById(idAssetInserito.toInt())
        assertEquals(0.2, assetDopoVendita.ASSE_qtaPosseduta, 0.0)
    }
}