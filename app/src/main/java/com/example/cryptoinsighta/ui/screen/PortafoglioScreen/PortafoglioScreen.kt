package com.example.cryptoinsighta.ui.screen.PortafoglioScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
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
import com.example.cryptoinsighta.viewmodel.PortafoglioViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun  CreaPortafoglioScreen(onClickVisualizzaMercato: () -> Unit, onClickRigaOpenDetailAsset:(Int)->Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val viewModel: PortafoglioViewModel = viewModel()
        val assetsPosseduti by viewModel.assetsPosseduti.collectAsState()
        val valoreAssetsAttuale by viewModel.valAssetPosseduto.collectAsState()
        val pnlPercentuali by viewModel.pnlPercentuali.collectAsState()
        val valorePortafoglioTotale by viewModel.valoreTotale.collectAsState()
        val pnlPortafoglioTotale by viewModel.pnlTotale.collectAsState()
        val pnlPortafoglioPercentualeTotale by viewModel.pnlPercentualeTotale.collectAsState()
        val listaGiornoValorePortafoglio by viewModel.listaGiornoValorePortafoglio.collectAsState()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            item {
                CreaAssetPerformance(
                    valorePortafoglio = valorePortafoglioTotale,
                    pnlTotale = pnlPortafoglioTotale,
                    pnlPercentuale = pnlPortafoglioPercentualeTotale
                )
            }
            item {
                CreaChart(
                    prezziStorici = listaGiornoValorePortafoglio
                )
            }
            item {
                CreaListaAssetsPosseduti(
                    assetsPosseduti, valoreAssetsAttuale, pnlPercentuali, onClickVisualizzaMercato,
                    onClickRigaOpenDetailAsset = onClickRigaOpenDetailAsset,
                    onClickEliminaAsset = { assetSelezionato -> viewModel.onClickEliminaAsset(assetSelezionato) }
                )
            }
        }
    }
}
