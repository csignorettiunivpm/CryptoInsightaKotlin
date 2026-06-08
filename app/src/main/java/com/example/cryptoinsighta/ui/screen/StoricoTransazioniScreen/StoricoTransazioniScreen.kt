package com.example.cryptoinsighta.ui.screen.StoricoTransazioniScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptoinsighta.ui.screen.MercatoScreen.CreaListaEsploraAssets
import com.example.cryptoinsighta.viewmodel.AssetDetailViewModel
import com.example.cryptoinsighta.viewmodel.TransactionHistoryViewModel

@Composable
fun CreaStoricoTransazioniScreen(){
    val viewModel: TransactionHistoryViewModel = viewModel()
    val listaTransazioni by viewModel.listaTransazioni.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.loadTransazioni()  // carica subito
    }


    Column(Modifier.fillMaxWidth().padding(16.dp)){
        Text(
            text = "Storico Transazioni",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer (Modifier.height(4.dp))
        Text(
            text = "Rivedi le tue operazioni effettuate sui vari assets.",
        )
        Spacer (Modifier.height(4.dp))
        CreaListaStoricoTotaleTransazioni(listaTransazioni, onEliminaClick = { transazione ->
            viewModel.eliminaTransazione(transazione)
        })
    }
}