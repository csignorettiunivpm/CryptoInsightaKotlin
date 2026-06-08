package com.example.cryptoinsighta.ui.screen.DettaglioAssetScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoinsighta.ui.theme.CustomDarkBlue
import com.example.cryptoinsighta.ui.theme.LightGray
import com.example.cryptoinsighta.model.Asset
import com.example.cryptoinsighta.ui.theme.LightGreen
import com.example.cryptoinsighta.ui.theme.negativeTrend
import com.example.cryptoinsighta.ui.theme.negativeTrendSfondo
import com.example.cryptoinsighta.ui.theme.positiveTrend
import com.example.cryptoinsighta.ui.theme.positiveTrendSfondo

@Composable
fun CreaCardPosizioniAsset(asset: Asset, mediaAcquisto:Double, totInvestimento:Double, PnLValore:Double, PnLPercentuale: Double) {
    val cardColors = CardDefaults.cardColors(
        containerColor = CustomDarkBlue
    )
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal=16.dp), shape = RoundedCornerShape(16.dp)){
        Column(modifier = Modifier.padding(16.dp)){
            Row(verticalAlignment = Alignment.CenterVertically ){
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = "Icona posizioni",
                    tint = MaterialTheme.colorScheme.onSurface // Imposta il colore coerente con il testo
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Le tue posizioni",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(12.dp) ){
                //box sinistro
                Card(
                    modifier = Modifier.weight(1f), // Divide lo spazio a metà
                    shape = RoundedCornerShape(8.dp),
                    colors = cardColors
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Quantità posseduta",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${String.format("%,.4f", asset.ASSE_qtaPosseduta)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Box Destro
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = cardColors
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Media acquisto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "€${String.format("%,.2f", mediaAcquisto)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
            }

            // Spazio verticale tra la prima riga e il box grande in basso
            Spacer(modifier = Modifier.height(12.dp))

            // box inferiorire
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = cardColors
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Investimento totale",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "€${String.format("%,.2f", totInvestimento)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 12.dp,bottom = 12.dp),
                thickness = 2.dp,
                color = Color.Gray
            )

            // Etichetta "P/L" in alto
            Text(
                text = "P/L",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp),
                fontWeight = FontWeight.Bold
            )

            // i
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cifra monetaria
                Text(
                    text = "€${if (PnLValore >= 0) "+" else ""}${String.format("%,.2f", PnLValore)}",
                    color = if(PnLValore >= 0) positiveTrend else negativeTrend,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                //spinge tutto ciò che viene dopo al limite destro della Row
                Spacer(modifier = Modifier.weight(1f))

                //  Il Badge della percentuale a destra
                Box(
                    modifier = Modifier
                        .background(
                            color = if(PnLValore >= 0) positiveTrendSfondo else negativeTrendSfondo ,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${if (PnLValore >= 0) "+" else ""}${String.format("%,.2f", PnLPercentuale)}%",
                        color = if(PnLValore >= 0) positiveTrend else negativeTrend,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
    }
}