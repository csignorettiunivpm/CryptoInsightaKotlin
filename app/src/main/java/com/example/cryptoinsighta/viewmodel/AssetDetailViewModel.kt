package com.example.cryptoinsighta.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoinsighta.model.Asset
import com.example.cryptoinsighta.model.PriceAlert
import com.example.cryptoinsighta.model.PriceHistory
import com.example.cryptoinsighta.model.Transaction
import com.example.cryptoinsighta.repository.AssetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.forEach


import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter


class AssetDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AssetRepository(application)

    private val _asset = MutableStateFlow<Asset?>(null)
    val asset: StateFlow<Asset?> = _asset

    // Espone alla View il prezzo corrente specifico di questo asset
    private val _prezzoCorrenteAsset = MutableStateFlow<Double?>(null)
    val prezzoCorrenteAsset: StateFlow<Double?> = _prezzoCorrenteAsset
    private val _prezziDisponibiliAsset = MutableStateFlow<List<PriceHistory>>(emptyList())
    val prezziDisponibiliAsset: StateFlow<List<PriceHistory>> = _prezziDisponibiliAsset
    private val _variazioneAsset = MutableStateFlow<Double?>(null)
    val variazioneAsset: StateFlow<Double?> = _variazioneAsset
    private val _pnlPercentualeAsset = MutableStateFlow<Double>(0.0)
    val pnlPercentualeAsset: StateFlow<Double> = _pnlPercentualeAsset

    private val _pnlPercentualeMioAndamento = MutableStateFlow<Double>(0.0)
    val pnlPercentualeMioAndamento: StateFlow<Double> = _pnlPercentualeMioAndamento

    private val _pnlValoreMioAndamento = MutableStateFlow<Double>(0.0)
    val pnlValoreMioAndamento: StateFlow<Double> = _pnlValoreMioAndamento
    private val _investimentoTotale = MutableStateFlow<Double>(0.0)
    val investimentoTotale: StateFlow<Double> = _investimentoTotale

    private val _mediaAccquisto = MutableStateFlow<Double>(0.0)
    val mediaAccquisto: StateFlow<Double> = _mediaAccquisto

    private val _listaTransazioniAsset = MutableStateFlow<List<Transaction>>(emptyList())
    val listaTransazioniAsset: StateFlow<List<Transaction>> = _listaTransazioniAsset

    private val _listaAvvisi = MutableStateFlow<List<PriceAlert>>(emptyList())
    val listaAvvisi: StateFlow<List<PriceAlert>> = _listaAvvisi
    fun loadAsset(assetId: Int) {
        viewModelScope.launch {
            try {
                // Con context Recupero l'asset dal DB in modo asincrono
                val assetRecuperato = withContext(Dispatchers.IO) {
                    repository.getAssetById(assetId)
                }
                _asset.value = assetRecuperato
                _listaTransazioniAsset.value = withContext(Dispatchers.IO){ repository.getTransactionsByAsset(assetId)}
                _listaAvvisi.value = withContext(Dispatchers.IO){repository.getAlertsByAssetId(assetId)}
                Log.d("appl", "${_listaTransazioniAsset.value}")


                // Recupera la lista dei prezzi come lista normale
                val listaPrezzi = withContext(Dispatchers.IO) {
                    repository.getPriceHistoryByAsset(assetRecuperato.ASSE_Id)
                }

                _prezziDisponibiliAsset.value = listaPrezzi
                Log.d("appl", "Prezzi caricati dal DB: ${listaPrezzi.size} elementi per l'asset $assetId")

                // Se ci sono prezzi, prendi l'ultimo e fai i calcoli del PnL
                if (listaPrezzi.isNotEmpty()) {
                    val ultimoPrezzoDisponibile = listaPrezzi.last()

                    _prezzoCorrenteAsset.value = ultimoPrezzoDisponibile.PRHI_prezzo

                    aggiornaPnLAsset(assetRecuperato, ultimoPrezzoDisponibile)
                    aggiornaPnLMioAndamento(assetRecuperato, ultimoPrezzoDisponibile)
                } else {
                    Log.d("appl", "Nessuno storico prezzi trovato nel DB per questo asset")
                }

            } catch (e: Exception) {
                Log.e("appl", "Errore nel caricamento dell'asset e del PnL: ${e.message}")
                e.printStackTrace()
            }
        }
    }




    private suspend fun aggiornaPnLMioAndamento(asset: Asset, prezzoCorrente: PriceHistory) {

        val transazioni:List<Transaction> = repository.getTransactionsByAsset(asset.ASSE_Id)
        val buyTransactions:List<Transaction> = transazioni.filter { it.TRAN_tipo == "BUY" }
        val sellTransactions:List<Transaction> = transazioni.filter { it.TRAN_tipo == "SELL" }

        var spesaTotaleReale = 0.0
        var qtaAssetAcquistata = 0.0
        var qtaAssetTotale = 0.0
        buyTransactions.forEach { transazione ->
            qtaAssetAcquistata += transazione.TRAN_qta
            spesaTotaleReale += transazione.TRAN_qta * transazione.TRAN_prezzoUnitario
        }
        qtaAssetTotale = qtaAssetAcquistata
        sellTransactions.forEach { transazione ->
            qtaAssetTotale -= transazione.TRAN_qta
        }

        val prezzoMedio = spesaTotaleReale / qtaAssetAcquistata
        val pnlValore = (prezzoCorrente.PRHI_prezzo - prezzoMedio) * asset.ASSE_qtaPosseduta
        // La percentuale di performance della posizione rimane identica
        val pnlPercentuale = if (prezzoMedio > 0) (prezzoCorrente.PRHI_prezzo - prezzoMedio)/prezzoMedio  * 100 else 0.0

        _pnlPercentualeMioAndamento.value = pnlPercentuale
        _pnlValoreMioAndamento.value = pnlValore
        Log.d("appl","${pnlValore}")
        _investimentoTotale.value = spesaTotaleReale
        _mediaAccquisto.value = prezzoMedio



    }

    private suspend fun aggiornaPnLAsset(asset: Asset, prezzoCorrente: PriceHistory) {
        val prezzoIeriAggiornato = repository.getYesterdayPrice(asset.ASSE_Id, prezzoCorrente.PRHI_dataOra)
        Log.d("appl", "ultimotimestamp ${prezzoCorrente.PRHI_dataOra} prezzoIeri: ${prezzoIeriAggiornato}, CORRENTE ${prezzoCorrente}")
        var pnlPercentuale = 0.0
        var variazioneAsset = 0.0
        if (prezzoCorrente != null && prezzoIeriAggiornato != null && prezzoIeriAggiornato.PRHI_prezzo > 0.0) {
            pnlPercentuale = ((prezzoCorrente.PRHI_prezzo - prezzoIeriAggiornato.PRHI_prezzo) / prezzoIeriAggiornato.PRHI_prezzo) * 100
            variazioneAsset = prezzoCorrente.PRHI_prezzo - prezzoIeriAggiornato.PRHI_prezzo
        } else {
            pnlPercentuale = 0.0 // Valore di default se non ci sono abbastanza dati storici nel database, non credo ma per sicurezza si fa
            variazioneAsset = 0.0
        }
        _pnlPercentualeAsset.value = pnlPercentuale
        _variazioneAsset.value = variazioneAsset
    }

     fun eliminaTransazione(transaction: Transaction){
         //viewModelScope.launch serve per dire alla view (sincrona) te intanto fai altro
         // tipo intanto mostra il caricamento, non aspettare me, altrimenti ti bloccheresti
         // quando ho finito ti avviso e te ti aggiorni
         viewModelScope.launch {
             repository.eliminaTransazione(transaction)
             _listaTransazioniAsset.value= repository.getTransactionsByAsset(transaction.TRAS_Id)
             //loadAsset(transaction.TRAS_Id)
         }

    }

    fun creaAvvisoPrezzo(asset: Asset?, percentuale: String, tipo: String) {
        viewModelScope.launch {
            val prezzoCorrente = _prezzoCorrenteAsset.value
            val valPercentuale = percentuale.toDouble()
            val moltiplicatore = valPercentuale / 100.0

            val prezzoSoglia = if (tipo == "Aumenta") {
                prezzoCorrente!! * (1.0 + moltiplicatore)
            } else {
                prezzoCorrente!! * (1.0 - moltiplicatore)
            }

            val objNotifica = PriceAlert(
                PRAS_Id = asset!!.ASSE_Id,
                PRAL_prezzoSoglia = prezzoSoglia,
                PRAL_tipoSoglia = tipo,
                PRAL_isAttivo = true
            )

            repository.insertAvviso(objNotifica)
            _listaAvvisi.value = repository.getAlertsByAssetId(asset.ASSE_Id) //non serve Dispatchers è già suspend la funzione con viewmodelscope.launch
        }
    }

    fun eliminaAvvisoPrezzo(avviso: PriceAlert) {
        viewModelScope.launch {
            repository.eliminaAvvisoPrezzo(avviso)
            _listaAvvisi.value = repository.getAlertsByAssetId(avviso.PRAS_Id) //non serve Dispatchers è già suspend la funzione con viewmodelscope.launch
        }
    }

}