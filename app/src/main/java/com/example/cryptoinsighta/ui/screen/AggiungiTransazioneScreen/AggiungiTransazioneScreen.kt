package com.example.cryptoinsighta.ui.screen.AggiungiTransazioneScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.cryptoinsighta.ui.theme.CustomDarkBlue
import com.example.cryptoinsighta.ui.theme.LightGray
import com.example.cryptoinsighta.ui.theme.LightGreen
import com.example.cryptoinsighta.ui.theme.negativeTrend
import com.example.cryptoinsighta.ui.theme.positiveTrend
import com.example.cryptoinsighta.viewmodel.AddTransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreaAggiungiTransazioneScreen(assetId:Int, onBackClick: () -> Unit){
    val viewModel: AddTransactionViewModel = viewModel() // ho dovuto far così perchè altrimenti se dovevo passare un parametro in più al viewmodel (assetId) mi serviva una factory --> così nessuna factory
    val prezzoGiorno by viewModel.prezzoGiorno.collectAsState()
    val isLoadingPrezzo by viewModel.isLoadingPrezzo.collectAsState()
    val errore by viewModel.errore.collectAsState()

    // date picker
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false)}
    LaunchedEffect(assetId) {
        viewModel.loadAsset(assetId)  // carica subito
    }
    val asset by viewModel.asset.collectAsState()
    if (asset == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val calcolaTotale = remember(viewModel.quantita, prezzoGiorno) {
        val qta = viewModel.quantita.replace(",", ".").toDoubleOrNull() ?: 0.0
        val prezzo = prezzoGiorno ?: 0.0
        qta * prezzo
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { onBackClick() }) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Indietro"
                            )
                        }
                    },
                    title = {
                        Text(text = "Aggiungi transazione")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                HorizontalDivider(
                    thickness = 3.dp,
                    color = Color.LightGray
                )
            }
        },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)){
                Text("VALORE TOTALE")
                Spacer (Modifier.height(4.dp))
                Text(
                    text = "€${String.format("%,.2f", calcolaTotale)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            viewModel.salvaTransazione(asset!!) {
                                onBackClick()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LightGreen, contentColor = CustomDarkBlue),
                        shape = RoundedCornerShape(12.dp),
                        enabled = run {
                            val qtaInserita = viewModel.quantita.replace(",", ".").toDoubleOrNull() ?: 0.0

                            if (viewModel.tipoTransazione == "SELL") {
                                (asset!!.ASSE_qtaPosseduta - qtaInserita) >= 0 && qtaInserita > 0.0
                            } else {
                                qtaInserita > 0.0
                            }
                        }
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Salva Transazione")
                    }

                }
            }
        },
        containerColor = MaterialTheme.colorScheme.primary
    )
    //calcola in automatico lo spazio rimanente tra la topBar e bottomBar
    { paddingValues ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //ASSET indicator
            Text(
                text = "ASSET: ${asset!!.ASSE_ticker}", // sicuramente ho l'asset perchè l'ho caricato aspettando (!!)
                style = MaterialTheme.typography.labelMedium,
                color = White
            )

            // selettore BUY / SELL
            Text(
                text = "TIPO TRANSAZIONE",
                style = MaterialTheme.typography.labelSmall,
                color = White
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                listOf("BUY", "SELL").forEach { tipo ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (viewModel.tipoTransazione == tipo && tipo=="BUY")
                                {
                                    positiveTrend
                                }else if (viewModel.tipoTransazione == tipo && tipo=="SELL"){
                                    negativeTrend
                                }
                                else Color.Transparent
                            )
                            .clickable { viewModel.tipoTransazione = tipo }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tipo,
                            color = if (viewModel.tipoTransazione == tipo)
                                MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }


            // quantità
            OutlinedTextField(
                value = viewModel.quantita,
                onValueChange = { viewModel.quantita = it },
                label = { Text("QUANTITÀ") },
                placeholder = { Text("0.00, per il separatore usa '.' non ','") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,   // Sfondo quando ci clicchi sopra
                    unfocusedContainerColor = Color.White, // Sfondo quando non è selezionato

                    // Opzionale: definisci i colori del testo e dei bordi per farli risaltare sul bianco
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            if(viewModel.tipoTransazione =="SELL")
            {
                Text(
                    text = "QTA massima vendibile: ${asset!!.ASSE_qtaPosseduta}",
                    style = MaterialTheme.typography.labelMedium,
                    color = White
                )
            }


            // data con date picker
            OutlinedTextField(
                value = viewModel.data,
                onValueChange = {
                    val timestampSelezionato = datePickerState.selectedDateMillis

                    // Passa il dato al ViewModel che ricalcolerà il prezzo e di conseguenza il totale
                    viewModel.onDataSelezionata(asset!!, timestampSelezionato!!)

                    showDatePicker = false
                },
                label = { Text("DATA") },
                placeholder = { Text("MM/DD/YYYY") },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,   // Sfondo quando ci clicchi sopra
                    unfocusedContainerColor = Color.White, // Sfondo quando non è selezionato

                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                ),
                trailingIcon = {
                    //sul click di sta casella faccio vedere il datepicker (sotto c'è il suo dialog)
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleziona data")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )
            // date picker dialog
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Annulla")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // prezzo recuperato automaticamente
            OutlinedTextField(
                value = if (isLoadingPrezzo) {
                    "Recupero prezzo..."
                } else if (prezzoGiorno != null) {
                    "€${String.format("%,.2f", prezzoGiorno)}"
                } else {
                    ""
                },
                onValueChange = {},
                label = { Text("PREZZO PER UNITÀ") },
                readOnly = true,
                enabled = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LightGray,   // Sfondo quando ci clicchi sopra
                    unfocusedContainerColor = LightGray, // Sfondo quando non è selezionato

                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )

            // errore
            errore?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // note
            OutlinedTextField(
                value = viewModel.note,
                onValueChange = { viewModel.note = it },
                label = { Text("NOTE (OPZIONALI)") },
                placeholder = { Text("Aggiungi qualche dettaglio su questa transazione...") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,   // Sfondo quando ci clicchi sopra
                    unfocusedContainerColor = Color.White, // Sfondo quando non è selezionato


                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )


        }

    }
    // quando cambia la data nel picker  --> recupera il prezzo
    //Il LaunchedEffect è perfetto perché ascolta i cambiamenti reali del valore: ogni volta che l'utente seleziona un giorno diverso, il timestamp cambia,
    //non a caso come con il valueChange
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { dataMillis ->
            viewModel.data = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                .format(Date(dataMillis))
            viewModel.onDataSelezionata(asset!!, dataMillis)
        }
    }
}