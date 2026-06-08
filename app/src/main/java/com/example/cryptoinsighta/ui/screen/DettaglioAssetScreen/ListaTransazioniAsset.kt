package com.example.cryptoinsighta.ui.screen.DettaglioAssetScreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoinsighta.model.Transaction
import com.example.cryptoinsighta.ui.components.CreaRigaTransazione
import com.example.cryptoinsighta.ui.theme.CustomDarkBlue
import com.example.cryptoinsighta.ui.theme.LightGreen

@Composable
fun CreaListaTransazioniAsset(transazioni:List<Transaction>, assetTicker:String, onEliminaClick:(Transaction) -> Unit, onAggiungiClick:() -> Unit) {
    // Definizione dei pesi (weight) per allineare l'intestazione e le righe in modo identico
    val pesoTipo = 0.22f
    val pesoData = 0.23f
    val pesoQta = 0.17f
    val pesoPrezzo = 0.23f
    val pesoAzioni = 0.15f


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Transazioni recenti",
            style = typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // --- INTESTAZIONE DELLA TABELLA ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tipo",
                color = Color.LightGray.copy(alpha = 0.6f),
                fontSize = 13.sp,
                modifier = Modifier.weight(pesoTipo),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Data",
                color = Color.LightGray.copy(alpha = 0.6f),
                fontSize = 13.sp,
                modifier = Modifier.weight(pesoData),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Qta",
                color = Color.LightGray.copy(alpha = 0.6f),
                fontSize = 13.sp,
                modifier = Modifier.weight(pesoQta),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Prezzo",
                color = Color.LightGray.copy(alpha = 0.6f),
                fontSize = 13.sp,
                modifier = Modifier.weight(pesoPrezzo),
                textAlign = TextAlign.End
            )
            Text(
                text = "Azioni",
                color = Color.LightGray.copy(alpha = 0.6f),
                fontSize = 13.sp,
                modifier = Modifier.weight(pesoAzioni),
                textAlign = TextAlign.End
            )
        }

        HorizontalDivider(thickness = 0.5.dp, color = Color.White.copy(alpha = 0.15f))

        // --- LISTA DELLE TRANSAZIONI ---
        if (transazioni.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nessuna transazione effettuata per ${assetTicker}",
                    color = White
                )
            }
        }else{

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
            ) {
                items(transazioni) { transazione ->
                    Log.d("appl","aee")
                    CreaRigaTransazione(
                        transazione = transazione,
                        tickerAsset = assetTicker,
                        pesoTipo = pesoTipo,
                        pesoData = pesoData,
                        pesoQta = pesoQta,
                        pesoPrezzo = pesoPrezzo,
                        pesoAzioni = pesoAzioni,
                        onEliminaClick = { onEliminaClick(transazione) }
                    )

                    HorizontalDivider(thickness = 1.5.dp, color = Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


        }
        // --- PULSANTE AGGIUNGI ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onAggiungiClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = LightGreen, contentColor = CustomDarkBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crea Transazione")
            }

        }

    }

}