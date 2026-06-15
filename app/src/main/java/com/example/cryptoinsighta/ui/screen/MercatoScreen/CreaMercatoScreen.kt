package com.example.cryptoinsighta.ui.screen.MercatoScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptoinsighta.viewmodel.PortafoglioViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreaMercatoScreen( onAggiungiClick: (Int) -> Unit, onClickRigaOpenDetailAsset: (Int) ->Unit) {
    val viewModel: PortafoglioViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    val assetsNonPosseduti = uiState.assetsNonPosseduti
    val prezziCorrenti = uiState.prezziCorrenti
    val pnlPercentuali = uiState.pnlPercentuali

    Column(Modifier.fillMaxWidth().padding(16.dp)){
        Text(
            text = "Esplora Assets",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer (Modifier.height(4.dp))
        Text(
            text = "Scopri e aggiungi nuovi investimenti al tuo portafoglio.",
        )
        Spacer (Modifier.height(4.dp))
        CreaListaEsploraAssets(
            assetsNonPosseduti, prezziCorrenti, pnlPercentuali,
            onAggiungiClick = { idAsset -> onAggiungiClick(idAsset) },
            onClickRigaOpenDetailAsset = { idAsset -> onClickRigaOpenDetailAsset(idAsset) },
        )
    }


}