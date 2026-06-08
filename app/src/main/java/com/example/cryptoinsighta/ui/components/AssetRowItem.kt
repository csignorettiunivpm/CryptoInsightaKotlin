package com.example.cryptoinsighta.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoinsighta.model.Asset
import coil.compose.AsyncImage
import com.example.cryptoinsighta.ui.theme.CustomDarkBlue
import com.example.cryptoinsighta.ui.theme.LightGray
import com.example.cryptoinsighta.ui.theme.bluChiaro
import com.example.cryptoinsighta.ui.theme.negativeTrend
import com.example.cryptoinsighta.ui.theme.positiveTrend

@Composable
fun creaAssetRiga(
    asset: Asset,
    valoreAsset: Double,
    pnlPercentuale: Double,
    tipoLista: String = "OWNED",
    onAggiungiClick: (Int) -> Unit,
    onClickRigaOpenDetailAsset: (Int) -> Unit,
    onClickEliminaAsset: ((Asset) -> Unit)? = null
)  {

    var mostraDialogConferma by remember { mutableStateOf(false) }
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = bluChiaro, shape = RoundedCornerShape(12.dp))
            .clickable( onClick = {onClickRigaOpenDetailAsset(asset.ASSE_Id)} )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!asset.ASSE_iconURL.isNullOrBlank()) {
            // Se c'è l'URL, prova a caricare l'immagine da internet
            AsyncImage(
                model = asset.ASSE_iconURL,
                contentDescription = "Logo ${asset.ASSE_nome}",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            // Se l'URL non c'è, mostra il Box con la lettera iniziale del ticker
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer), // Cambiato in container per un contrasto migliore col testo
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = asset.ASSE_ticker.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))

        //qui metto il ticker e il nome dell'asset
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = asset.ASSE_nome,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Surface(
                    shape = RoundedCornerShape(8.dp), // Angoli arrotondati
                    color = MaterialTheme.colorScheme.primaryContainer, // Colore di sfondo del badge
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer // Colore del testo interno

                ) {
                    Text(

                        modifier = Modifier.padding(4.dp),
                        text = asset.ASSE_ticker,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = asset.ASSE_categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = White
                )
            }

        }

        var colore = positiveTrend
        var segno = "+"
        if(pnlPercentuale < 0)
        {
            colore = negativeTrend
            segno = ""
        }

        //qui metto il valore e il P&L e in caso ci trovassimo nell'esplora lista anche il buttone "+"
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "€${String.format("%,.2f", valoreAsset)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Text(
                text = "$segno${String.format("%.1f", pnlPercentuale)}%",
                style = MaterialTheme.typography.bodySmall,
                color = colore
            )
            if(tipoLista.uppercase() == "ESPLORA")
            {
                FloatingActionButton(
                    onClick = { onAggiungiClick(asset.ASSE_Id) },
                    modifier = Modifier.size(30.dp),
                    shape = CircleShape,                   // Forza la forma a cerchio perfetto
                    containerColor = LightGray,
                    contentColor = CustomDarkBlue
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        modifier = Modifier.size(20.dp),
                        contentDescription = "Aggiungi nuovo elemento"
                    )
                }
            }
        }
        if(asset.ASSE_qtaPosseduta > 0)
        {
            // Colonna Azioni
            Box(
                contentAlignment = Alignment.CenterEnd,
            ) {
                IconButton(
                    onClick = { mostraDialogConferma = true },
                    modifier = Modifier.padding(12.dp)
                        .size(28.dp)
                        .background(negativeTrend, RoundedCornerShape(6.dp))
                ) {
                    Text(
                        text = "x",
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(y = (-1).dp)
                    )
                }
            }
        }


        if (mostraDialogConferma) {
            AlertDialog(
                onDismissRequest = { mostraDialogConferma = false },
                title = { Text(text = "Elimina Transazione", fontWeight = FontWeight.Bold) },
                text = { Text(text = "Sei sicuro di voler eliminare l'asset ${asset.ASSE_nome}:${asset.ASSE_ticker} dal tuo portafoglio? verranno eliminate anche tutte le transazioni ad esso associate") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onClickEliminaAsset?.invoke(asset)
                            mostraDialogConferma = false
                        }
                    ) {
                        Text("Elimina", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostraDialogConferma = false }) {
                        Text("Annulla")
                    }
                }
            )
        }
    }
}