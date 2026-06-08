package com.example.cryptoinsighta.navigation

//sealed class sta per: classe sigillata, serve per dire al compilatore che le uniche pagine esistenti nella mia app sono quelle qui sotto
//di base si usa per 2 motivi fondamentali:
//sicurezza: quando dovrò gestire i click dell'utente per cambiare schermata, Kotlin saprà esattamente quali sono le pagine totali
//mi permette di usare ad esempio Screen.Portafoglio.route evitando ogni errore di battitura durante la scrittura del percorso
//centralizza i dati: tutte le schermate condividono "route" che serve a Compose per capire dove andare
sealed class Screen(val route: String) {
    object Portafoglio : Screen("portafoglio")
    object Mercato : Screen("mercato")
    object Storico : Screen("storico")
    object AggiungiTransazione : Screen("aggiungi_transazione/{assetId}")
    object ApriDettaglioAsset : Screen("apri_dettaglio_asset/{assetId}")

}