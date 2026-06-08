package com.example.cryptoinsighta.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Scroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptoinsighta.model.PriceHistory
import com.example.cryptoinsighta.ui.theme.backgroundPeriodColor
import com.example.cryptoinsighta.ui.theme.periodColor
import com.patrykandpatrick.vico.*
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost

import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CreaChart(prezziStorici: List<PriceHistory>)
{
    //per grafico Vico
    val modelProducer = remember { CartesianChartModelProducer() }
    var periodoSelezionato by remember { mutableStateOf("1D") }
    val zoomState = rememberVicoZoomState(
        initialZoom = Zoom.Content
    )

    val prezziFiltratiPerPeriodo = remember(prezziStorici, periodoSelezionato) {
        if (prezziStorici.isEmpty()) return@remember emptyList()

        val cal = Calendar.getInstance()
        val adesso = cal.timeInMillis

        when (periodoSelezionato.uppercase()) {
            "1W" -> cal.add(Calendar.WEEK_OF_YEAR, -1)
            "1M" -> cal.add(Calendar.MONTH, -1)
            "1Y" -> cal.add(Calendar.YEAR, -1)
            else -> return@remember prezziStorici
        }
        //dipendentemente da cosa preme l'utente si ha ho settimana -1 o mese -1 o anno -1
        val inizioTimestamp = cal.timeInMillis

        return@remember prezziStorici.filter { it.PRHI_dataOra in inizioTimestamp..adesso }
    }

    LaunchedEffect(prezziFiltratiPerPeriodo) {
        if (prezziFiltratiPerPeriodo.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(prezziFiltratiPerPeriodo.map { it.PRHI_prezzo })
                }
            }
        }
    }


    val periodi = listOf("1D","1W","1M","1Y")

    val formatterPeriodo = remember(prezziFiltratiPerPeriodo, periodoSelezionato) {
        CartesianValueFormatter { context, x, verticalAxisPosition ->
            // 'x' ( in Vico 2 rappresenta la coordinata)
            val index = x.toInt()

            if (index in prezziFiltratiPerPeriodo.indices) {
                val timestamp = prezziFiltratiPerPeriodo[index].PRHI_dataOra
                val pattern = when (periodoSelezionato) {
                    "1D" -> "dd MMM HH:mm"
                    "1W", "1M" -> "dd MMM"
                    else -> "MMM/yy"
                }
                SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))
            } else {
                " "
            }
        }
    }
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal=16.dp), shape = RoundedCornerShape(16.dp)){
        Column(modifier = Modifier.padding(16.dp)){
            Text(
                text = "Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // adesso creo il selettore del periodo
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(backgroundPeriodColor).padding(vertical = 6.dp).height(25.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                periodi.forEach { periodo ->
                    val isSelected = periodo == periodoSelezionato
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable {
                            periodoSelezionato = periodo
                        }.padding(horizontal = 16.dp, vertical = 5.dp)
                    ){ //il quadratino deve avvolgermi il testo dei periodi!!!!
                        Text(
                            text = periodo,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else periodColor,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                }
            }

            //adesso creo il grafico
            Spacer(modifier = Modifier.height(8.dp))
            if (prezziStorici.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nessun dato disponibile",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                // qui integro Vico
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CartesianChartHost(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        modelProducer = modelProducer,
                        scrollState = rememberVicoScrollState(scrollEnabled = true),
                        zoomState = zoomState,
                        chart = rememberCartesianChart(

                            rememberLineCartesianLayer(
                                lineProvider = LineCartesianLayer.LineProvider.series(
                                    listOf(
                                        LineCartesianLayer.rememberLine(
                                            fill = LineCartesianLayer.LineFill.single(
                                                com.patrykandpatrick.vico.compose.common.fill(Color.Green)
                                            )
                                        )
                                    )
                                )
                            ),
                            // sse Y (Prezzi a sinistra)
                            startAxis = VerticalAxis.rememberStart(

                                valueFormatter = CartesianValueFormatter { _, value, _ ->
                                    // Ora 'value' è garantito essere il Double/Float del prezzo
                                    "€${String.format(Locale.US, "%,.2f", value)}"
                                }
                            ),
                            // Asse X (Tempo in basso)
                            bottomAxis = HorizontalAxis.rememberBottom(
                                valueFormatter = formatterPeriodo
                            )
                        )
                    )
                }
            }
        }
    }
}