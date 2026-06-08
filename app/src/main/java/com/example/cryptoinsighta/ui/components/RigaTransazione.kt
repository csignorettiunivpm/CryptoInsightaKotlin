package com.example.cryptoinsighta.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoinsighta.model.Transaction
import com.example.cryptoinsighta.ui.theme.LightGray
import com.example.cryptoinsighta.ui.theme.negativeTrend
import com.example.cryptoinsighta.ui.theme.negativeTrendSfondo
import com.example.cryptoinsighta.ui.theme.positiveTrend
import com.example.cryptoinsighta.ui.theme.positiveTrendSfondo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreaRigaTransazione(transazione: Transaction, tickerAsset: String, pesoTipo: Float, pesoData:Float, pesoQta:Float, pesoPrezzo:Float, pesoAzioni:Float, onEliminaClick:(Transaction)-> Unit){
    val formattaData = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }
    val dataLeggibile = formattaData.format(Date(transazione.TRAN_dataOra))
    val isBuy = transazione.TRAN_tipo == "BUY"
    val coloreSegno = if (isBuy) positiveTrend else negativeTrend
    val coloreSegnoSfondo = if (isBuy) positiveTrendSfondo else negativeTrendSfondo
    var mostraDialogConferma by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colonna Tipo
        Row(
            modifier = Modifier.weight(pesoTipo),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(coloreSegnoSfondo, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBuy) "+" else "−",
                    color = coloreSegno,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Text(text = if (isBuy) "Buy" else "Sell", color = White, fontSize = 14.sp)
        }

        // Colonna Data
        Text(
            text = dataLeggibile,
            color = Color.LightGray,
            fontSize = 13.sp,
            modifier = Modifier.weight(pesoData),
            textAlign = TextAlign.Center
        )

        // Colonna Quantità
        Column(
            modifier = Modifier.weight(pesoQta),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("%.2f", transazione.TRAN_qta),
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(text = tickerAsset, color = LightGray.copy(alpha = 0.6f), fontSize = 11.sp)
        }

        // Colonna Prezzo Unitario
        Text(
            text = "€${String.format("%,.2f", transazione.TRAN_prezzoUnitario)}",
            color = LightGray,
            fontSize = 14.sp,
            modifier = Modifier.weight(pesoPrezzo),
            textAlign = TextAlign.End
        )

        // Colonna Azioni
        Box(
            modifier = Modifier.weight(pesoAzioni),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                onClick = { mostraDialogConferma = true },
                modifier = Modifier
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
            text = { Text(text = "Sei sicuro di voler eliminare questa operazione di ${transazione.TRAN_tipo} da ${transazione.TRAN_qta} $tickerAsset?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminaClick(transazione)
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