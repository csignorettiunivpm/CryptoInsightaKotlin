package com.example.cryptoinsighta.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoinsighta.database.AppDatabase
import com.example.cryptoinsighta.model.Asset
import com.example.cryptoinsighta.model.PriceHistory
import com.example.cryptoinsighta.model.Transaction
import com.example.cryptoinsighta.repository.AssetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

//Il contesto Application vive per tutta la durata dell'app.
//E' sicuro da tenere nel ViewModel perché non rischia di rompersi o causare memory leak0
// durante ad es rotazini schermo.

//Ereditando da AndroidViewModel, all'interno dei metodi del ViewModel posso usare in qualunque momento la funzione getApplication()
//per ottenere il contesto da passare a Room, alle SharedPreferences o per leggere stringhe di sistema.
val assetsDiTest = listOf(
    Asset(ASSE_nome = "Bitcoin", ASSE_ticker = "BTC-EUR", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.0, ASSE_categoria = "Crypto"),
    Asset(ASSE_nome = "Ethereum", ASSE_ticker = "ETH-EUR", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.0, ASSE_categoria = "Crypto"),
    Asset(ASSE_nome = "Apple Inc.", ASSE_ticker = "AAPL", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.0, ASSE_categoria = "Azione"),
    Asset(ASSE_nome = "Tesla Inc.", ASSE_ticker = "TSLA", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.0, ASSE_categoria = "Azione"),
    Asset(ASSE_nome = "Microsoft Corp.", ASSE_ticker = "MSFT", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.0, ASSE_categoria = "Azione"),
    Asset(ASSE_nome = "Amazon.com Inc.", ASSE_ticker = "AMZN", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.0, ASSE_categoria = "Azione"),
    Asset(ASSE_nome = "Alphabet Inc. (Google)", ASSE_ticker = "GOOGL", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.0, ASSE_categoria = "Azione"),
    Asset(ASSE_nome = "Meta Platforms Inc.", ASSE_ticker = "META", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.0, ASSE_categoria = "Azione"),
    Asset(ASSE_nome = "Netflix Inc.", ASSE_ticker = "NFLX", ASSE_iconURL = "", ASSE_qtaPosseduta = 0.0, ASSE_categoria = "Azione")
)
data class PortafoglioUiState(
    val assets: List<Asset> = emptyList(),
    val assetsPosseduti: List<Asset> = emptyList(),
    val assetsNonPosseduti: List<Asset> = emptyList(),
    val prezziCorrenti: Map<Int, Double> = emptyMap(),
    val pnlPercentuali: Map<Int, Double> = emptyMap(),
    val valAssetPosseduto: Map<Int, Double> = emptyMap(),
    val valoreTotale: Double = 0.0,
    val pnlTotale: Double = 0.0,
    val pnlPercentualeTotale: Double = 0.0,
    val listaGiornoValorePortafoglio: List<PriceHistory> = emptyList()
)
@RequiresApi(Build.VERSION_CODES.O)
class PortafoglioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AssetRepository(application)


    private val _uiState = MutableStateFlow(PortafoglioUiState())
    val uiState: StateFlow<PortafoglioUiState> = _uiState.asStateFlow()




    //all'avvio del view model ci si mette in ascolto del DB e si popolano tutte le mappe sopra dichiarate che altrimenti sarebbero vuote
    init {

        viewModelScope.launch {

            val sharedPreferences = getApplication<Application>()
                .getSharedPreferences("ImpostazioniApp", Context.MODE_PRIVATE)

            // Controlliamo se è il primo avvio assoluto
            val isPrimoAvvio = sharedPreferences.getBoolean("primo_avvio_eseguito", true)

            if (isPrimoAvvio) {
                Log.d("appl","solo una volta")
                // pulisco le tabelle e inserisco i dati iniziali a quota 0
                popolaTabAssets()

                // Salvo nelle SharedPreferences che il primo avvio è stato completato
                sharedPreferences.edit()
                    .putBoolean("primo_avvio_eseguito", false)
                    .apply() // .apply() salva in modo asincrono senza bloccare l'app
            }

            init()


        }
    }

    private suspend fun init() {
        val listaCompleta = repository.getAllAssets()

        _uiState.update { statoAttuale ->
            statoAttuale.copy(
                assets = listaCompleta,
                assetsPosseduti = listaCompleta.filter { it.ASSE_qtaPosseduta > 0.0 },
                assetsNonPosseduti = listaCompleta.filter { it.ASSE_qtaPosseduta == 0.0 }
            )
        }


        //assets.collect dice: "Ogni volta che la lista degli asset nel database cambia (o viene caricata per la prima volta),
        // dammi la nuova listaAssets e fai partire il codice all'interno

        listaCompleta.forEach { asset ->
            aggiornaPrezzoAsset(asset)
        }
        calcolaStatisticheTotali(listaCompleta)
        popolaListaGiornoValorePortafoglio()
    }

    suspend fun popolaTabAssets() {
        repository.svuotaTabelle()
        assetsDiTest.forEach { asset ->
            repository.insertAsset(asset)

        }
    }
    private suspend fun popolaListaGiornoValorePortafoglio(){
        val transazioniTotali = repository.getAlTransactions().sortedBy { it.TRAN_dataOra }
        var valorePortafoglio = 0.0
        if (transazioniTotali.isEmpty()) return
        val cinqueGiorniInMilli = 5L * 24 * 60 * 60 * 1000
        val lista = mutableListOf(
            PriceHistory(PRAS_Id = 0, PRHI_prezzo = 0.0, PRHI_dataOra = transazioniTotali.first().TRAN_dataOra - cinqueGiorniInMilli )
        )


        val mappaQuantitaAsset = mutableMapOf<Int, Double>()
        transazioniTotali.forEachIndexed  { index, transazione ->

            val assetId = transazione.TRAS_Id
            val quantitaPrecedente = mappaQuantitaAsset.getOrDefault(assetId, 0.0)

            // Recuperiamo la quantità attuale di questo specifico asset
            val controvaloreTransazione = transazione.TRAN_qta * transazione.TRAN_prezzoUnitario

            if (transazione.TRAN_tipo == "BUY") {
                // Compri: aumenta l'asset
                mappaQuantitaAsset[assetId] = quantitaPrecedente + transazione.TRAN_qta
                //valorePortafoglio -= controvaloreTransazione
            } else {
                // Vendi: diminuisce l'asset, ma i soldi entrano nella liquidità in cassa (non vanno persi!)
                mappaQuantitaAsset[assetId] = quantitaPrecedente - transazione.TRAN_qta
                valorePortafoglio += controvaloreTransazione
            }


            if(index < transazioniTotali.lastIndex)
            {
                val transazioneSuccessiva = transazioniTotali[index + 1]
                if (transazione.TRAN_dataOra == transazioneSuccessiva.TRAN_dataOra) {
                    return@forEachIndexed //continue
                }
            }

            var valoreTotalePortafoglioQuelGiorno = valorePortafoglio

            // Cicliamo su tutti gli asset accumulati fino ad ora per calcolare il controvalore totale
            mappaQuantitaAsset.forEach { (idAsset, quantitaPosseduta) ->

                val prezzoAttualeDiQuestoAsset = repository.getLastPrice(idAsset)

                valoreTotalePortafoglioQuelGiorno += (quantitaPosseduta * prezzoAttualeDiQuestoAsset!!.PRHI_prezzo)
            }


            lista.add(PriceHistory(0,0,valoreTotalePortafoglioQuelGiorno,transazione.TRAN_dataOra))

        }
        _uiState.update { statoAttuale ->
            statoAttuale.copy(listaGiornoValorePortafoglio = lista)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun aggiornaPrezzoAsset(asset: Asset) {
        //qui già controllo se ho prezzi nel db per quell'asset e se sono più vecchi della data attuale
        repository.recuperaEMemorizzaPrezziAssetByAsset(asset)

        // aggiorna la mappa con l'ultimo prezzo disponibile nel DB

        val prezzoAggiornato = repository.getLastPrice(asset.ASSE_Id)

        if(prezzoAggiornato != null){
            _uiState.update { statoAttuale ->
                val nuovaMappa = statoAttuale.prezziCorrenti.toMutableMap()
                nuovaMappa[asset.ASSE_Id] = prezzoAggiornato.PRHI_prezzo
                statoAttuale.copy(prezziCorrenti = nuovaMappa)
            }
            //adesso aggiorno le statistiche dell'asset
            aggiornaPnLAsset(asset, prezzoAggiornato)
        }
    }

    private suspend fun aggiornaPnLAsset(asset: Asset, prezzoCorrente: PriceHistory) {

        //val transazioni:List<Transaction> = listaTransazioniDiEsempio

        if(asset.ASSE_qtaPosseduta > 0)
        {
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

            val pnlMonetarioSulleQuoteRimaste = prezzoCorrente.PRHI_prezzo  * qtaAssetTotale

            val prezzoMedio = spesaTotaleReale / qtaAssetAcquistata

            // La percentuale di performance della posizione rimane identica
            val pnlPercentuale = if (prezzoMedio > 0) (prezzoCorrente.PRHI_prezzo - prezzoMedio)/prezzoMedio  * 100 else 0.0


            // Ora aggiorniamo la mappa per la UI di Compose
            _uiState.update { statoAttuale ->
                val nuovaMappaPnl = statoAttuale.pnlPercentuali.toMutableMap()
                val nuovaMappaValori = statoAttuale.valAssetPosseduto.toMutableMap()

                nuovaMappaPnl[asset.ASSE_Id] = pnlPercentuale
                nuovaMappaValori[asset.ASSE_Id] = pnlMonetarioSulleQuoteRimaste

                statoAttuale.copy(
                    pnlPercentuali = nuovaMappaPnl,
                    valAssetPosseduto = nuovaMappaValori
                )
            }
        }else{

            val prezzoIeriAggiornato = repository.getYesterdayPrice(asset.ASSE_Id, prezzoCorrente.PRHI_dataOra)
            Log.d("appl", "ultimotimestamp ${prezzoCorrente.PRHI_dataOra} prezzoIeri: ${prezzoIeriAggiornato}, CORRENTE ${prezzoCorrente}")
            val pnlPercentuale = if (prezzoCorrente != null && prezzoIeriAggiornato != null && prezzoIeriAggiornato.PRHI_prezzo > 0.0) {
                ((prezzoCorrente.PRHI_prezzo - prezzoIeriAggiornato.PRHI_prezzo) / prezzoIeriAggiornato.PRHI_prezzo) * 100
            } else {
                0.0 // Valore di default se non ci sono abbastanza dati storici nel database, non credo ma per sicurezza si fa
            }


            _uiState.update { statoAttuale ->
                val nuovaMappaPnl = statoAttuale.pnlPercentuali.toMutableMap()
                nuovaMappaPnl[asset.ASSE_Id] = pnlPercentuale
                statoAttuale.copy(pnlPercentuali = nuovaMappaPnl)
            }
        }


    }

    private suspend fun calcolaStatisticheTotali(listaAssets: List<Asset>) {
        var totaleInvestito = 0.0
        var totaleAttualePortafoglio = 0.0



        listaAssets.forEach { asset ->
            val prezzoCorrente = _uiState.value.prezziCorrenti[asset.ASSE_Id] ?: 0.0


            val transactions = repository.getTransactionsByAsset(asset.ASSE_Id)
            //il prezzo corrente dell'asset per la quantità che te hai + quello che hai GIà venduto
            totaleAttualePortafoglio += (prezzoCorrente * asset.ASSE_qtaPosseduta) + (transactions.filter { it.TRAN_tipo == "SELL" }.sumOf { it.TRAN_qta * it.TRAN_prezzoUnitario })
            totaleInvestito += transactions.filter { it.TRAN_tipo == "BUY" }.sumOf { it.TRAN_qta * it.TRAN_prezzoUnitario }
        }
        val calcoloPnlTotale = totaleAttualePortafoglio - totaleInvestito
        val calcoloPnlPercentualeTotale = if (totaleInvestito > 0) (calcoloPnlTotale / totaleInvestito) * 100 else 0.0
        _uiState.update { statoAttuale ->
            statoAttuale.copy(
                valoreTotale = totaleAttualePortafoglio,
                pnlTotale = calcoloPnlTotale,
                pnlPercentualeTotale = calcoloPnlPercentualeTotale
            )
        }
    }

    fun onClickEliminaAsset(asset: Asset) {
        viewModelScope.launch {
            repository.eliminaAsset(asset)
            init()
        }
    }

}