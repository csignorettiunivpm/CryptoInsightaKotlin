package com.example.cryptoinsighta.ui.screen.MercatoScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cryptoinsighta.model.Asset
import com.example.cryptoinsighta.ui.components.creaAssetRiga

@Composable
fun CreaListaEsploraAssets(listaAsset: List<Asset>, prezziCorrenti: Map<Int,Double>, pnlPercentuali: Map<Int,Double>,  onAggiungiClick: (Int) -> Unit, onClickRigaOpenDetailAsset:(Int)->Unit){

    Card(modifier = Modifier.fillMaxWidth().padding(horizontal=16.dp), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Spacer(modifier = Modifier.height(12.dp))

            if (listaAsset.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nessun asset disponibile",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    // PEZZO FONDAMENTALE: Usiamo 'items' anziché il forEach dentro un item
                    // gni riga verrà renderizzata SOLO quando entra nello schermo!
                    items(listaAsset) { assetNonPosseduto ->
                        creaAssetRiga(assetNonPosseduto, prezziCorrenti[assetNonPosseduto.ASSE_Id] ?: 0.0, pnlPercentuali[assetNonPosseduto.ASSE_Id] ?: 0.0, "ESPLORA",
                            { onAggiungiClick(assetNonPosseduto.ASSE_Id) },
                            { onClickRigaOpenDetailAsset(assetNonPosseduto.ASSE_Id) }) {  }
                    }
                }
//                listaAsset.forEach { assetNonPosseduto ->
//
//                    creaAssetRiga(assetNonPosseduto, prezziCorrenti[assetNonPosseduto.ASSE_Id] ?: 0.0, pnlPercentuali[assetNonPosseduto.ASSE_Id] ?: 0.0, "ESPLORA",
//                        { onAssetSelezionato(assetNonPosseduto.ASSE_Id) },
//                        { onClickRigaOpenDetailAsset(assetNonPosseduto.ASSE_Id) }) {  }
//                }
            }



        }
    }
}