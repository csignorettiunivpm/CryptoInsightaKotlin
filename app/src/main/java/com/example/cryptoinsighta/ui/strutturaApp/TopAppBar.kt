package com.example.cryptoinsighta.ui.strutturaApp

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class) //OptIn serve per far sì che io possa compilare perchè questa di Material è una libreria ancora in fase sperimentale
@Composable //dice ad Android che questa è una funzione che genera una UI grafica, non fa quindi calcoli
fun CreaTopAppBar(currentRoute: String?, onBackClick:() -> Unit){
    Column {
        TopAppBar(
            title = {
                Text(text = "CryptoInsighta")
            },
            navigationIcon = {
                if (currentRoute == "apri_dettaglio_asset") {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = Color.White
                        )
                    }
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifiche"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        HorizontalDivider(
            thickness = 3.dp,
            color = Color.LightGray
        )
    }
}