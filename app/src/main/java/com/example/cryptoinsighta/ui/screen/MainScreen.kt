package com.example.cryptoinsighta.ui.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cryptoinsighta.navigation.Screen
import com.example.cryptoinsighta.ui.screen.AggiungiTransazioneScreen.CreaAggiungiTransazioneScreen
import com.example.cryptoinsighta.ui.screen.DettaglioAssetScreen.CreaDettaglioAssetScreen

import com.example.cryptoinsighta.ui.screen.MercatoScreen.CreaMercatoScreen
import com.example.cryptoinsighta.ui.screen.PortafoglioScreen.CreaPortafoglioScreen
import com.example.cryptoinsighta.ui.screen.StoricoTransazioniScreen.CreaStoricoTransazioniScreen
import com.example.cryptoinsighta.ui.strutturaApp.CreaBottomAppBar
import com.example.cryptoinsighta.ui.strutturaApp.CreaTopAppBar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun  CreaMainScreen() {
    //È l'oggetto che si occupa di cambiare schermata, tenere traccia della cronologia tipo se si preme il tasto "indietro" del telefono e gestire i passaggi.
    // La parola remember serve a fare in modo che se il telefono ruota, la navigazione non si azzera.
    val navController = rememberNavController()
    //osserva la cronologia delle pagine. Ogni volta che cambio pagina, questa riga viene notificata e si accorge del cambiamento e avvisa l'interfaccia grafica.
    val currentBackStack by navController.currentBackStackEntryAsState()
    //nome della pagina attualmente visibile
    val currentRoute = currentBackStack?.destination?.route


    //Scaffold (di Material 3) mette a disposizione una struttura standard, riserva un posto alla mia top bar e bottom bar e ai pezzi nel mezzo standard
    Scaffold(
        topBar = {

            val sonoNelleSchermateVietate = currentRoute?.startsWith("aggiungi_transazione") == true || currentRoute?.startsWith("apri_dettaglio_asset") == true
            if (!sonoNelleSchermateVietate) {
                CreaTopAppBar(currentRoute, onBackClick = { navController.navigate(Screen.Mercato.route)})
            }

        },
        bottomBar = {
            if (currentRoute?.startsWith("aggiungi_transazione") == false) {
                CreaBottomAppBar(
                    currentRoute = currentRoute,
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.primary

    )
    //calcola in automatico lo spazio rimanente tra la topBar e bottomBar
    {
        paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Portafoglio.route,
            modifier = Modifier.padding(paddingValues)
        ){
            composable(Screen.Portafoglio.route) {
                CreaPortafoglioScreen(
                    onClickVisualizzaMercato = {
                        navController.navigate(Screen.Mercato.route)
                    },
                    onClickRigaOpenDetailAsset = {idAssetCliccato ->
                        navController.navigate("apri_dettaglio_asset/$idAssetCliccato")

                    }
                )
            }
            composable(Screen.Mercato.route) {
                CreaMercatoScreen(onAggiungiClick = { idAssetCliccato ->
                    navController.navigate("aggiungi_transazione/$idAssetCliccato")
                },onClickRigaOpenDetailAsset = {idAssetCliccato ->
                    navController.navigate("apri_dettaglio_asset/$idAssetCliccato")

                })
            }
            composable(Screen.Storico.route) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CreaStoricoTransazioniScreen()
                }
            }
            composable(
                route = Screen.AggiungiTransazione.route,
                arguments = listOf(navArgument("assetId") { type = NavType.IntType })
            )
            { backStackEntry -> val idAssetRicevuto = backStackEntry.arguments?.getInt("assetId") ?: return@composable

                CreaAggiungiTransazioneScreen(
                    assetId = idAssetRicevuto, onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ApriDettaglioAsset.route,
                arguments = listOf(navArgument("assetId") { type = NavType.IntType })
            )

            { backStackEntry -> val idAssetRicevuto = backStackEntry.arguments?.getInt("assetId") ?: return@composable

                CreaDettaglioAssetScreen(
                    assetId = idAssetRicevuto, onBackClick = { navController.popBackStack()}, onClickAggiungiTransazione = { navController.navigate("aggiungi_transazione/$idAssetRicevuto") }
                )
            }
        }
    }
}