package com.example.cryptoinsighta.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.cryptoinsighta.model.modelsFinanceAPI.PrezzoDTO
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class YahooFinanceService {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun ottieniPrezziAsset(ticker: String, fromDateMillis: Long? ): List<PrezzoDTO> {

        var periodoDaPrendere: Long? = null
        //se è null significa che ancora il db sui prezzi su sto ticker è vuoto, quindi

        //per prendere i dati da queste api bisogna convertire i timestamp in /1000
        if (fromDateMillis == null) {
            //provo a prendere fino a 1 anni fa
            periodoDaPrendere = (System.currentTimeMillis() / 1000) - (365L * 24 * 60 * 60)
        } else {
            periodoDaPrendere = fromDateMillis / 1000
        }

        val adesso = System.currentTimeMillis() / 1000




        Log.d("appl", "PERIODO DA PRENDERE E ADESSO ${periodoDaPrendere}, ${adesso} ")


        return chiamaApiEMappaRisposta(ticker,periodoDaPrendere,adesso)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun ottieniPrezziAssetDaDataFinoAPrimaDataDisponibile(ticker: String, fromDateMillis: Long?, toDateMillis:Long? ): List<PrezzoDTO> {

        //per prendere i dati da queste api bisogna convertire i timestamp in /1000
        val periodoDaPrendere = fromDateMillis!! / 1000
        var toDateMillis = toDateMillis
        if(toDateMillis != null)
        {
            toDateMillis /= 1000
        }else{
            toDateMillis = System.currentTimeMillis() / 1000
        }



        val dataInizioLeggibile = LocalDateTime.ofInstant(Instant.ofEpochMilli(periodoDaPrendere*1000), ZoneId.systemDefault())
        val dataFineLeggibile = LocalDateTime.ofInstant(Instant.ofEpochMilli(toDateMillis*1000), ZoneId.systemDefault())

        // Stampa il log con le date convertite
        Log.d("appl", "PERIODO DA PRENDERE E ADESSO $dataInizioLeggibile, $dataFineLeggibile")

        return chiamaApiEMappaRisposta(ticker, fromDateMillis, toDateMillis)

    }

    private suspend fun chiamaApiEMappaRisposta(ticker:String, periodoDaPrendere:Long, toDateMillis:Long): List<PrezzoDTO> {
        //qui sostanzialmente sto chiamando le api
        val response = RetrofitInstance.api.getPrices(
            ticker = ticker,
            period1 = periodoDaPrendere,
            period2 = toDateMillis
        )

        val result = response.chart.result?.firstOrNull() ?: return emptyList()
        val timestamps = result.timestamp ?: return emptyList()
        val closes = result.indicators.quote.firstOrNull()?.close ?: return emptyList()

        // .zip combina timestamp e prezzi in una lista di PrezzoDTO
        var coppieTimeStampPrezzo = timestamps.zip(closes)

        coppieTimeStampPrezzo = coppieTimeStampPrezzo.filter { (_, close) -> close != null }  // filtra i giorni senza prezzo (festivi)
        // il "_" si usa per dire al compilatore di ignorare il timestamp in questo caso
        // e mi prende solo i prezzi di chiusura "close" che sono diversi da null

        return coppieTimeStampPrezzo.map { (timestamp, close) ->
                //Log.d("appl", " ${ticker} - Chiamo API.")
                PrezzoDTO(
                    timestamp = timestamp * 1000,  // riconverto in millisecondi
                    close = close!! //close sarù sicuramente non nullo perchè l'ho controllato sopra
                )
            }
    }

}