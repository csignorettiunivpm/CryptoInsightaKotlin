package com.example.cryptoinsighta.ui.screen.DettaglioAssetScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.colors
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoinsighta.model.Asset
import com.example.cryptoinsighta.model.PriceAlert
import com.example.cryptoinsighta.model.PriceHistory
import com.example.cryptoinsighta.ui.theme.CustomDarkBlue
import com.example.cryptoinsighta.ui.theme.negativeTrend
import com.example.cryptoinsighta.ui.theme.positiveTrend

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreaCardAvvisoPrezzo(asset: Asset, prezzoAttuale: Double, listaAvvisiAttivi: List<PriceAlert>, onImpostaAvvisoClick: (String, String) -> Unit, onEliminaAvvisoClick:(PriceAlert)->Unit ){
    var percentualeSoglia by remember { mutableStateOf("") }
    var prezzoSoglia: Double by remember { mutableStateOf(0.0) }
    var expanded by remember { mutableStateOf(false) }
    var direzioneSelezionata by remember { mutableStateOf("Aumenta") }
    val coloreTrend = if (direzioneSelezionata.contains("Aumenta")) positiveTrend else negativeTrend
    var mostraDialogConferma by remember { mutableStateOf(false) }
    var avvisoDaEliminare by remember { mutableStateOf<PriceAlert?>(null) }


    if(mostraDialogConferma && avvisoDaEliminare != null){
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Elimina Avviso", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Sei sicuro di voler eliminare questo avviso ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminaAvvisoClick(avvisoDaEliminare!!)
                        mostraDialogConferma = false
                        avvisoDaEliminare = null
                    }
                ) {
                    Text("Elimina", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {  mostraDialogConferma = false
                    avvisoDaEliminare = null}) {
                    Text("Annulla")
                }
            }
        )
    }
    fun aggiornaPrezzoSoglia(percentualeStr: String, direzione: String) {
        val percentuale = percentualeStr.toDoubleOrNull() ?: 0.0
        val moltiplicatore = percentuale / 100.0

        prezzoSoglia = if (direzione == "Aumenta") {
            prezzoAttuale * (1.0 + moltiplicatore)
        } else {
            prezzoAttuale * (1.0 - moltiplicatore)
        }
    }



    Card(modifier = Modifier.fillMaxWidth().padding(horizontal=16.dp), shape = RoundedCornerShape(16.dp)){
        Column(modifier = Modifier.padding(16.dp),verticalArrangement = Arrangement.spacedBy(16.dp)){
            Row(verticalAlignment = Alignment.CenterVertically ){
                Icon(
                    imageVector = Icons.Default.AddAlert,
                    contentDescription = "Icona avviso",
                    tint = MaterialTheme.colorScheme.onSurface // Imposta il colore coerente con il testo
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Avvisi di prezzo",
                    style = MaterialTheme.typography.titleMedium,

                )
            }
            Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(12.dp) ){
                Text(
                    text = "Ottieni una notifica quando ${asset.ASSE_ticker} supera o perde una certa percentuale dal prezzo attuale",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp //per il testo a capo
                )

            }
            Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(12.dp) ){
                OutlinedTextField(
                    value = percentualeSoglia,
                    onValueChange = { input ->
                        if (input.isEmpty()) {
                            percentualeSoglia = ""

                            aggiornaPrezzoSoglia("", direzioneSelezionata)
                        } else {
                            val inputFormattato = input.replace(',', '.')
                            val valoreNumerico = inputFormattato.toDoubleOrNull()
                            if (valoreNumerico != null && valoreNumerico in 0.0..100.0) {
                                percentualeSoglia = inputFormattato

                                aggiornaPrezzoSoglia(inputFormattato, direzioneSelezionata)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(0.35f)
                        .height(56.dp), // Altezza standard abbinata al bottone
                    prefix = {
                        Text(text = "% ", color = White.copy(alpha = 0.6f))
                    },
                    placeholder = {
                        Text(text = "20", color = White.copy(alpha = 0.3f))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp),
                    colors = colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedContainerColor = CustomDarkBlue.copy(alpha = 0.5f),
                        unfocusedContainerColor = CustomDarkBlue.copy(alpha = 0.5f),
                        focusedBorderColor = White.copy(alpha = 0.4f),
                        unfocusedBorderColor = White.copy(alpha = 0.2f)
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                //Select box verso alto o basso
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .weight(0.65f)
                        .height(56.dp)
                ) {
                    OutlinedTextField(
                        value = direzioneSelezionata,
                        onValueChange = {

                        },
                        readOnly = true, // Impedisce la scrittura da tastiera
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.menuAnchor(), // Collega il menu al campo di testo
                        colors = colors(
                            focusedTextColor = coloreTrend,
                            unfocusedTextColor = coloreTrend,
                            focusedContainerColor = CustomDarkBlue.copy(alpha = 0.5f),
                            unfocusedContainerColor = CustomDarkBlue.copy(alpha = 0.5f),
                            focusedBorderColor = coloreTrend,
                            unfocusedBorderColor = coloreTrend
                        )
                    )

                    // Opzioni del menu a tendina
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Aumenta (▲)") },
                            onClick = {
                                direzioneSelezionata = "Aumenta"
                                expanded = false
                                aggiornaPrezzoSoglia(percentualeSoglia, direzioneSelezionata)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Diminuisce (▼)") },
                            onClick = {
                                direzioneSelezionata = "Diminuisce"
                                expanded = false
                                aggiornaPrezzoSoglia(percentualeSoglia, direzioneSelezionata)
                            }
                        )
                    }
                }

            }


            Spacer(modifier = Modifier.width(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(12.dp) ){
                Button(
                    onClick = {
                        if (percentualeSoglia.isNotEmpty()) {
                            onImpostaAvvisoClick(percentualeSoglia, direzioneSelezionata)
                        }
                    },
                    modifier = Modifier.height(56.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC5CBE9),
                        contentColor = Color(0xFF1A237E)
                    )
                ) {
                    Text(
                        text = "Imposta avviso",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }


            Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(12.dp) ){

                if(percentualeSoglia.isNotEmpty())
                {
                    if(direzioneSelezionata == "Aumenta")
                    {
                        Text(
                            text = "La notifica verrà inviata quando il prezzo di ${asset.ASSE_ticker} sarà >= di € ${
                                String.format(
                                    "%,.2f",
                                    prezzoSoglia
                                )
                            }",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    else{
                        Text(
                            text = "La notifica verrà inviata quando il prezzo di ${asset.ASSE_ticker} sarà <= di € ${
                                String.format(
                                    "%,.2f",
                                    prezzoSoglia
                                )
                            }",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }


            }
            if (listaAvvisiAttivi.isNotEmpty()) {
                HorizontalDivider(color = White.copy(alpha = 0.1f))

                Text(
                    text = "Avvisi attivi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // La LazyColumn per stampare ogni riga degli avvisi attivi
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 250.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = listaAvvisiAttivi,
                        key = { avviso -> avviso.PRAL_Id}
                    ) { avviso ->
                        val Aumento = avviso.PRAL_tipoSoglia == "Aumenta"
                        val coloreAvviso = if (Aumento) positiveTrend else negativeTrend
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Prezzo ${if (Aumento) ">= " else "<= "} € ${String.format("%,.2f", avviso.PRAL_prezzoSoglia)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "(${avviso.PRAL_tipoSoglia})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = coloreAvviso
                                )
                            }

                            IconButton(onClick = {
                                mostraDialogConferma=true
                                avvisoDaEliminare = avviso
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Elimina",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
