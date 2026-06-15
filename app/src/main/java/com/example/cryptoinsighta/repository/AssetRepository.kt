package com.example.cryptoinsighta.repository

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.cryptoinsighta.api.YahooFinanceService
import com.example.cryptoinsighta.dao.PriceHistoryDao
import com.example.cryptoinsighta.database.AppDatabase
import com.example.cryptoinsighta.model.Asset
import com.example.cryptoinsighta.model.PriceAlert
import com.example.cryptoinsighta.model.PriceHistory
import com.example.cryptoinsighta.model.Transaction
import com.example.cryptoinsighta.model.TransactionWithTicker
import com.example.cryptoinsighta.model.modelsFinanceAPI.PrezzoDTO
import com.example.cryptoinsighta.viewmodel.TransactionHistoryViewModel


class AssetRepository(context: Application) {
    private val db = AppDatabase.getInstance(context)
    private val priceHistoryDao: PriceHistoryDao = db.priceHistoryDao()
    private val transactionDao = db.transactionDao()
    private val assetDao = db.assetDao()
    private val priceAlertDao = db.priceAlertDao()
    private val yahooFinanceService = YahooFinanceService()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun recuperaEMemorizzaPrezziAssetByAssetAndDate(asset: Asset, dataRichiesta: Long){
        //per cercare di ovviare ad altre possibili richieste, non prendo solo quella data, ma da quella data alla prima data disponibile nel mio DB
        // di base sto supponendo che l'utente richieda una data precedente a quelel salvate nel db -> non so se sia pericolo, vediamo
        val firstDBTimestampForThisAsset:Long? = priceHistoryDao.getFirstAvailableTimestamp(asset.ASSE_Id)
        var prezzi: List<PrezzoDTO> = yahooFinanceService.ottieniPrezziAssetDaDataFinoAPrimaDataDisponibile(asset.ASSE_ticker, dataRichiesta, firstDBTimestampForThisAsset)
        mappaEInserisciPrezzi(prezzi, asset, false)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun recuperaEMemorizzaPrezziAssetByAsset(asset: Asset){
        try{
            val lastDBPrice = priceHistoryDao.getLastPrice(asset.ASSE_Id)
            var prezzi: List<PrezzoDTO>? = null
            val adesso = System.currentTimeMillis()
            if(lastDBPrice==null)
            {
                //se non c'è alcun prezzo per quell'asset nel db significa che è la prima volta che visualizza l'asset, quindi prendo il max possibile di prezzi da api
                prezzi = yahooFinanceService.ottieniPrezziAsset(asset.ASSE_ticker, null)
                mappaEInserisciPrezzi(prezzi, asset)

                // 1_800_000 millisecondi = 30 minuti
            }else {
                val trentaMinutiFa = adesso - 1_800_000

                // Se l'ora salvata nel DB è MINORE di 30 minuti fa, significa che è VECCHIA
                if (lastDBPrice.PRHI_dataOra/1000 < trentaMinutiFa/1000) {

                    prezzi = yahooFinanceService.ottieniPrezziAsset(asset.ASSE_ticker, lastDBPrice.PRHI_dataOra)


                    // PASSO FONDAMENTALE: Passa l'ora attuale "adesso" alla funzione di salvataggio
                    mappaEInserisciPrezzi(prezzi, asset)
                } else {
                    Log.d("appl", "Dati freschi. Salto l'API.")
                }
            }



        }catch (e: Exception) {
            Log.d("appl","err - ${asset.ASSE_nome} - ${e}")
            // se la chiamata API fallisce, uso i dati già salvati
            e.printStackTrace()
        }

    }


    private suspend fun mappaEInserisciPrezzi(prezzi: List<PrezzoDTO>, asset: Asset, lastDateToActual: Boolean = true) {
        try{
            if(prezzi.isNullOrEmpty()) return

            val oraAttualeDelTelefono = System.currentTimeMillis()

            val prezziMappati = prezzi.mapIndexed { index, prezzo ->
                PriceHistory(
                    PRAS_Id = asset.ASSE_Id,
                    PRHI_prezzo = prezzo.close,

                    // !!!! Se è l'ultimo elemento della lista, salviamo l'ora del telefono.
                    PRHI_dataOra = if (index == prezzi.lastIndex && lastDateToActual) oraAttualeDelTelefono else prezzo.timestamp,
                )
            }
            priceHistoryDao.insertPrices(prezziMappati)


        }catch(e:Exception)
        {
            Log.d("appl", "${e}")
        }

    }

    suspend fun getLastPrice(assetId:Int): PriceHistory? {
        return priceHistoryDao.getLastPrice(assetId)
    }
    suspend fun getYesterdayPrice(assetId:Int, dataOra:Long): PriceHistory? {
        return priceHistoryDao.getYesterdayPrice(assetId, dataOra)
    }

    suspend fun getTransactionsByAsset(assetId: Int): List<Transaction> {
        return transactionDao.getTransactionsByAsset(assetId)
    }
    suspend fun insertTransaction(transazione: Transaction, qtaPossedutaAsset: Double): Unit {
        transactionDao.inserisciTransazioneEAgiornaAsset(transazione, qtaPossedutaAsset)
    }

    suspend fun eliminaTransazione(transaction: Transaction) {
        transactionDao.deleteTransactionEAggiornaAsset(transaction)
    }
    suspend fun getAssetById(assetId:Int): Asset{
        return assetDao.getAssetByIdOnce(assetId)
    }
    suspend fun getPriceHistoryByAsset(assetId:Int):List<PriceHistory>{
        return priceHistoryDao.getPriceHistoryByAsset(assetId)
    }
    suspend fun getAllAssets():List<Asset>{
        return assetDao.getAllAssets()
    }
    suspend fun svuotaTabella(){
        assetDao.svuotaTabella()
    }
    suspend fun insertAsset(asset: Asset){
        assetDao.insertAsset(asset)
    }
    suspend fun getPriceByDate(idAsset:Int, daData:Long, aData:Long): PriceHistory{
        return priceHistoryDao.getPriceByDate(idAsset,daData,aData)
    }

    suspend fun getAllTransactionsWithTicker(): List<TransactionWithTicker> {
        return transactionDao.getAllTransactionsWithTicker()
    }

    suspend fun getAlTransactions(): List<Transaction> {
        return transactionDao.getAllTransactions()
    }

    suspend fun insertAvviso(objNotifica: PriceAlert) {
        priceAlertDao.insertAlert(objNotifica)
    }

    suspend fun getAlertsByAssetId(assetId: Int): List<PriceAlert> {
        return priceAlertDao.getAlertsByAsset(assetId)
    }

    suspend fun eliminaAvvisoPrezzo(avviso: PriceAlert) {
        priceAlertDao.deleteAlert(avviso)
    }

    suspend fun svuotaTabelle() {
        assetDao.svuotaTabella()
        priceAlertDao.svuotaTabella()
        priceHistoryDao.svuotaTabella()
        transactionDao.svuotaTabella()
    }

    suspend fun eliminaAsset(asset: Asset) {
        assetDao.deleteAsset(asset);
        var newAsset = Asset(ASSE_nome = asset.ASSE_nome, ASSE_ticker = asset.ASSE_ticker, ASSE_iconURL = asset.ASSE_iconURL, ASSE_descrizione = asset.ASSE_descrizione, ASSE_qtaPosseduta = 0.0, ASSE_categoria = asset.ASSE_categoria);
        assetDao.insertAsset(newAsset)
    }


}