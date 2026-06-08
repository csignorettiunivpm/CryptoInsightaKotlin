package com.example.cryptoinsighta.ui.components


import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cryptoinsighta.model.Asset
import com.example.cryptoinsighta.ui.theme.LightGray
import com.example.cryptoinsighta.ui.theme.bluChiaro
import com.example.cryptoinsighta.ui.theme.negativeTrend
import com.example.cryptoinsighta.ui.theme.positiveTrend

@Composable
fun CreaAssetPerformance(valorePortafoglio: Double, pnlTotale: Double, pnlPercentuale: Double, asset: Asset? = null)
{
    var colore = positiveTrend
    var segno = "+"
    if(pnlTotale < 0)
    {
        colore = negativeTrend
        segno = ""
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (asset != null) { // se asset è valorizzato significa che sto arrivando dal dettaglio asset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!asset.ASSE_iconURL.isNullOrBlank()) {
                    AsyncImage(
                        model = asset.ASSE_iconURL,
                        contentDescription = "Logo ${asset.ASSE_nome}",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asset.ASSE_nome,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (asset == null) 8.dp else 0.dp)
        ) {
            if (asset == null) {
                Text(
                    text = "Valore del portafoglio totale",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = "€${String.format("%,.2f", valorePortafoglio)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$segno€${String.format("%,.2f", pnlTotale)} (${segno}${String.format("%.2f", pnlPercentuale)}%) Oggi",
                style = MaterialTheme.typography.bodyMedium,
                color = colore
            )
        }
    }

}