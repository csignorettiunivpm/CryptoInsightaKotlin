package com.example.cryptoinsighta.ui.screen.DettaglioAssetScreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptoinsighta.ui.components.CreaAssetPerformance
import com.example.cryptoinsighta.ui.components.CreaChart
import com.example.cryptoinsighta.ui.strutturaApp.CreaTopAppBar
import com.example.cryptoinsighta.viewmodel.AssetDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreaDettaglioAssetScreen(assetId:Int, onBackClick: () -> Unit, onClickAggiungiTransazione: (Int) -> Unit) {
    val viewModel: AssetDetailViewModel = viewModel()
    val asset by viewModel.asset.collectAsState()
    val prezzoAttuale by viewModel.prezzoCorrenteAsset.collectAsState()
    val pnlPercentualeAsset by viewModel.pnlPercentualeAsset.collectAsState()
    val variazioneAsset by viewModel.variazioneAsset.collectAsState()
    val pnlPercentualeMioAndamento by viewModel.pnlPercentualeMioAndamento.collectAsState()
    val pnlValoreMioAndamento by viewModel.pnlValoreMioAndamento.collectAsState()
    val investimentoTotale by viewModel.investimentoTotale.collectAsState()
    val mediaAccquisto by viewModel.mediaAccquisto.collectAsState()
    val prezziAsset by viewModel.prezziDisponibiliAsset.collectAsState()
    val listaTransazioniAsset by viewModel.listaTransazioniAsset.collectAsState()
    val listaAvvisi by viewModel.listaAvvisi.collectAsState()


    LaunchedEffect(assetId) {
        viewModel.loadAsset(assetId)  // carica subito
    }

    if (asset == null || prezzoAttuale == null || variazioneAsset == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            CreaTopAppBar("apri_dettaglio_asset", onBackClick = onBackClick)
        },
        containerColor = MaterialTheme.colorScheme.primary
    )
    //calcola in automatico lo spazio rimanente tra la topBar e bottomBar
    { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CreaAssetPerformance(prezzoAttuale!!, variazioneAsset!!,pnlPercentualeAsset, asset)
            CreaChart(
                prezziStorici = prezziAsset
            )
            if(asset!!.ASSE_qtaPosseduta > 0)
            {
                CreaCardPosizioniAsset(asset!!, mediaAccquisto, investimentoTotale,pnlValoreMioAndamento, pnlPercentualeMioAndamento )
            }
            CreaCardAvvisoPrezzo(asset!!, prezzoAttuale!!, listaAvvisi, onImpostaAvvisoClick = { percentuale, tipo -> viewModel.creaAvvisoPrezzo(asset, percentuale, tipo)}, onEliminaAvvisoClick = {avviso -> viewModel.eliminaAvvisoPrezzo(avviso)})
            CreaListaTransazioniAsset(listaTransazioniAsset, asset!!.ASSE_ticker, onEliminaClick ={ transazione ->
                viewModel.eliminaTransazione(transazione)
            },onAggiungiClick = {
                onClickAggiungiTransazione(assetId)
            })

        }
    }

}