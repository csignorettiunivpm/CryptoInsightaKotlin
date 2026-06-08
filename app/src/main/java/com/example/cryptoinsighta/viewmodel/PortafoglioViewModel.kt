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
@RequiresApi(Build.VERSION_CODES.O)
class PortafoglioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AssetRepository(application)


    private val _assets = MutableStateFlow<List<Asset>>(emptyList())
    val assets: StateFlow<List<Asset>> = _assets

    private val _assetsPosseduti = MutableStateFlow<List<Asset>>(emptyList())
    val assetsPosseduti: StateFlow<List<Asset>> = _assetsPosseduti

    private val _assetsNonPosseduti = MutableStateFlow<List<Asset>>(emptyList())
    val assetsNonPosseduti: StateFlow<List<Asset>> = _assetsNonPosseduti

    // mappa assetId -> ultimo prezzo disponibile da api
    //Questo è il pattern fondamentale dell'incapsulamento in Kotlin e serve a proteggere l'applicazione da bug
    // il MutableStateFlow serve per dire che solo il viewModel può modificarne il contenuto
    //lo StateFlow invece è pubblico non modificabile, Compose può leggerlo e rimanere in ascolto senza modificarlo per sbaglio

    //I prezzi in tempo reale vengono salvati temporaneamente solo nella RAM del telefono dentro questa mappa _prezziCorrenti
    private val _prezziCorrenti = MutableStateFlow<Map<Int, Double>>(emptyMap())
    val prezziCorrenti: StateFlow<Map<Int, Double>> = _prezziCorrenti

    //anche questi son tutti dati che mi cambiano dinamicamente in base ai prezzi attuali di mercato degli asset, quindi devo usare la stessa logica
    // mappa assetId -> P&L %
    private val _pnlPercentuali = MutableStateFlow<Map<Int, Double>>(emptyMap())
    val pnlPercentuali: StateFlow<Map<Int, Double>> = _pnlPercentuali

    private val _valAssetPosseduto = MutableStateFlow<Map<Int, Double>>(emptyMap())
    val valAssetPosseduto: StateFlow<Map<Int, Double>> = _valAssetPosseduto

    // valore totale portafoglio
    private val _valoreTotale = MutableStateFlow(0.0)
    val valoreTotale: StateFlow<Double> = _valoreTotale

    // P&L totale portafoglio
    private val _pnlTotale = MutableStateFlow(0.0)
    val pnlTotale: StateFlow<Double> = _pnlTotale

    // P&L % totale portafoglio
    private val _pnlPercentualeTotale = MutableStateFlow(0.0)
    val pnlPercentualeTotale: StateFlow<Double> = _pnlPercentualeTotale

    private val _listaGiornoValorePortafoglio = MutableStateFlow<List<PriceHistory>>(emptyList())
    val listaGiornoValorePortafoglio: StateFlow<List<PriceHistory>> = _listaGiornoValorePortafoglio

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

        _assets.value = listaCompleta
        _assetsPosseduti.value = listaCompleta.filter { it.ASSE_qtaPosseduta > 0.0 }
        _assetsNonPosseduti.value = listaCompleta.filter { it.ASSE_qtaPosseduta == 0.0 }


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
        _listaGiornoValorePortafoglio.value = lista

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun aggiornaPrezzoAsset(asset: Asset) {
        //qui già controllo se ho prezzi nel db per quell'asset e se sono più vecchi della data attuale
        repository.recuperaEMemorizzaPrezziAssetByAsset(asset)

        // aggiorna la mappa con l'ultimo prezzo disponibile nel DB

        val prezzoAggiornato = repository.getLastPrice(asset.ASSE_Id)

        if(prezzoAggiornato != null){
            _prezziCorrenti.update {  mappaCorrente ->

                val nuovaMappa = mappaCorrente.toMutableMap()


                nuovaMappa[asset.ASSE_Id] = prezzoAggiornato.PRHI_prezzo


                nuovaMappa
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
            _pnlPercentuali.update { mappaCorrente ->
                val nuovaMappa = mappaCorrente.toMutableMap()
                nuovaMappa[asset.ASSE_Id] = pnlPercentuale
                nuovaMappa
            }
            _valAssetPosseduto.update { mappaCorrente ->
                val nuovaMappa = mappaCorrente.toMutableMap()
                nuovaMappa[asset.ASSE_Id] = pnlMonetarioSulleQuoteRimaste
                nuovaMappa
            }
        }else{

            val prezzoIeriAggiornato = repository.getYesterdayPrice(asset.ASSE_Id, prezzoCorrente.PRHI_dataOra)
            Log.d("appl", "ultimotimestamp ${prezzoCorrente.PRHI_dataOra} prezzoIeri: ${prezzoIeriAggiornato}, CORRENTE ${prezzoCorrente}")
            val pnlPercentuale = if (prezzoCorrente != null && prezzoIeriAggiornato != null && prezzoIeriAggiornato.PRHI_prezzo > 0.0) {
                ((prezzoCorrente.PRHI_prezzo - prezzoIeriAggiornato.PRHI_prezzo) / prezzoIeriAggiornato.PRHI_prezzo) * 100
            } else {
                0.0 // Valore di default se non ci sono abbastanza dati storici nel database, non credo ma per sicurezza si fa
            }


            _pnlPercentuali.update { mappaCorrente ->
                val nuovaMappa = mappaCorrente.toMutableMap()
                nuovaMappa[asset.ASSE_Id] = pnlPercentuale
                nuovaMappa
            }
        }


    }

    private suspend fun calcolaStatisticheTotali(listaAssets: List<Asset>) {
        var totaleInvestito = 0.0
        var totaleAttualePortafoglio = 0.0



        listaAssets.forEach { asset ->
            val prezzoCorrente = _prezziCorrenti.value[asset.ASSE_Id] ?: 0.0


            val transactions = repository.getTransactionsByAsset(asset.ASSE_Id)
            //il prezzo corrente dell'asset per la quantità che te hai + quello che hai GIà venduto
            totaleAttualePortafoglio += (prezzoCorrente * asset.ASSE_qtaPosseduta) + (transactions.filter { it.TRAN_tipo == "SELL" }.sumOf { it.TRAN_qta * it.TRAN_prezzoUnitario })
            totaleInvestito += transactions.filter { it.TRAN_tipo == "BUY" }.sumOf { it.TRAN_qta * it.TRAN_prezzoUnitario }
        }
        _valoreTotale.value = totaleAttualePortafoglio
        _pnlTotale.value = totaleAttualePortafoglio - totaleInvestito
        _pnlPercentualeTotale.value = if (totaleInvestito > 0)
            ((totaleAttualePortafoglio - totaleInvestito) / totaleInvestito) * 100 else 0.0

    }

    fun onClickEliminaAsset(asset: Asset) {
        viewModelScope.launch {
            repository.eliminaAsset(asset)
            init()
        }
    }

}