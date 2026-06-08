package com.example.cryptoinsighta.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.cryptoinsighta.database.AppDatabase
import com.example.cryptoinsighta.model.Asset
import com.example.cryptoinsighta.model.Transaction
import com.example.cryptoinsighta.repository.AssetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddTransactionViewModel (application: Application) : AndroidViewModel(application) {
    private val repository = AssetRepository(application)

    private val _asset = MutableStateFlow<Asset?>(null)
    val asset: StateFlow<Asset?> = _asset

    // campi per l'inserimento della transazione
    var tipoTransazione by mutableStateOf("BUY")
    var quantita by mutableStateOf("")
    var data by mutableStateOf("")
    var note by mutableStateOf("")

    //VARIABILI OSSERVATE DALLA VIEW
    private val _prezzoGiorno = MutableStateFlow<Double?>(null)
    val prezzoGiorno: StateFlow<Double?> = _prezzoGiorno

    private val _isLoadingPrezzo = MutableStateFlow(false)
    val isLoadingPrezzo: StateFlow<Boolean> = _isLoadingPrezzo

    private val _errore = MutableStateFlow<String?>(null)
    val errore: StateFlow<String?> = _errore

    private val _dataSelezionataMillis = MutableStateFlow<Long?>(null)

    fun loadAsset(assetId: Int) {
        viewModelScope.launch {
            _asset.value = repository.getAssetById(assetId)
        }
    }
    // chiamata quando l'utente seleziona una data
    @RequiresApi(Build.VERSION_CODES.O)
    fun onDataSelezionata(asset: Asset, dataMillis: Long) {
        //Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a Job.
        //praticamente me permette di non bloccare l'app mentre cerco de recuperare il prezzo relativo alla data inserita dall'utente
        viewModelScope.launch {
            _isLoadingPrezzo.value = true
            _prezzoGiorno.value = null
            _errore.value = null
            _dataSelezionataMillis.value = dataMillis
            try{
                val daData = dataMillis
                val aData = dataMillis + (24 * 60 * 60 * 1000)
                var prezzo = repository.getPriceByDate(asset.ASSE_Id, daData, aData)
                if (prezzo == null) {
                    // non c'è nel DB allora scarica dall'API
                    repository.recuperaEMemorizzaPrezziAssetByAssetAndDate(asset, dataMillis)
                    prezzo = repository.getPriceByDate(asset.ASSE_Id, daData, aData)
                }
                if (prezzo != null) {
                    _prezzoGiorno.value = prezzo.PRHI_prezzo
                } else {
                    _errore.value = "Prezzo non disponibile per questa data"
                }
            }catch(e: Exception){
                _errore.value = "Errore nel recupero del prezzo, ${e}, probabilmente hai selezionato un giorno in cui il mercato era chiuso"
            }finally {
                _isLoadingPrezzo.value = false
            }
        }
    }

    fun salvaTransazione(asset:Asset, onSuccess: () -> Unit) {
        val qta = quantita.toDoubleOrNull()
        val prezzo = _prezzoGiorno.value

        if (qta == null || qta <= 0) {
            _errore.value = "Inserisci una quantità valida"
            return
        }
        if (prezzo == null) {
            _errore.value = "Seleziona una data valida"
            return
        }

        viewModelScope.launch {
            val transazione = Transaction(
                TRAS_Id = asset.ASSE_Id,
                TRAN_tipo = tipoTransazione,
                TRAN_qta = qta,
                TRAN_prezzoUnitario = prezzo,
                TRAN_dataOra = _dataSelezionataMillis.value!!,
                TRAN_note = note
            )
            repository.insertTransaction(transazione, asset.ASSE_qtaPosseduta)
            onSuccess()
        }
    }


}