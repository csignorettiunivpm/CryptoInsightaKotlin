package com.example.cryptoinsighta.ui.screen.PortafoglioScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
fun CreaListaAssetsPosseduti (assetsPosseduti: List<Asset>, valoreAssetsAttuale: Map<Int,Double>, pnlPercentuali: Map<Int,Double>,
                              onClickVisualizzaMercato: () -> Unit,
                              onClickRigaOpenDetailAsset:(idAsset:Int) -> Unit,
                              onClickEliminaAsset: (asset: Asset) -> Unit ) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal=16.dp), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Top assets posseduti", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (assetsPosseduti.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally){
                        Text(
                            text = "Nessun asset posseduto",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Button(onClick = {
                            onClickVisualizzaMercato()
                        }) {
                            Text(text = "Aggiungi assets")
                        }
                    }
                }
            } else {
                // Se ci sono elementi, usiamo la LazyColumn in modo nativo
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    // PEZZO FONDAMENTALE: Usiamo 'items' anziché il forEach dentro un item
                    // gni riga verrà renderizzata SOLO quando entra nello schermo!
                    items(assetsPosseduti) { assetPosseduto ->
                        creaAssetRiga(
                            assetPosseduto,
                            valoreAssetsAttuale[assetPosseduto.ASSE_Id] ?: 0.0,
                            pnlPercentuali[assetPosseduto.ASSE_Id] ?: 0.0,
                            onAggiungiClick = { },
                            onClickRigaOpenDetailAsset = { onClickRigaOpenDetailAsset(assetPosseduto.ASSE_Id) },
                            onClickEliminaAsset = { onClickEliminaAsset(assetPosseduto)}
                        )
                    }
                }
//                assetsPosseduti.forEach { assetPosseduto ->
//                    creaAssetRiga(
//                        assetPosseduto,
//                        valoreAssetsAttuale[assetPosseduto.ASSE_Id] ?: 0.0,
//                        pnlPercentuali[assetPosseduto.ASSE_Id] ?: 0.0,
//                        onAggiungiClick = { },
//                        onClickRigaOpenDetailAsset = { onClickRigaOpenDetailAsset(assetPosseduto.ASSE_Id) }) { }
//                }
            }
        }
    }


}