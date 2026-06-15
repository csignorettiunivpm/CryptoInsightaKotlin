package com.example.cryptoinsighta.ui.screen.PortafoglioScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptoinsighta.ui.components.CreaAssetPerformance
import com.example.cryptoinsighta.ui.components.CreaChart
import com.example.cryptoinsighta.viewmodel.PortafoglioViewModel
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker.ValueFormatter.Companion.default


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun  CreaPortafoglioScreen(onClickVisualizzaMercato: () -> Unit, onClickRigaOpenDetailAsset:(Int)->Unit) {
    val viewModel: PortafoglioViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)

    ){

        CreaAssetPerformance(
            valorePortafoglio = uiState.valoreTotale,
            pnlTotale = uiState.pnlTotale,
            pnlPercentuale = uiState.pnlPercentualeTotale
        )
        CreaChart(
            prezziStorici = uiState.listaGiornoValorePortafoglio
        )
        CreaListaAssetsPosseduti(
            uiState.assetsPosseduti, uiState.valAssetPosseduto, uiState.pnlPercentuali, onClickVisualizzaMercato,
            onClickRigaOpenDetailAsset = onClickRigaOpenDetailAsset,
            onClickEliminaAsset = { assetSelezionato -> viewModel.onClickEliminaAsset(assetSelezionato) }
        )

    }

}
