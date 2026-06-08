object CryptoCalc {


    fun IsBottoneAbilitato(
        quantitaTesto: String,
        tipoTransazione: String,
        quantitaPossedutaAsset: Double
    ): Boolean {
        // Converte la stringa gestendo sia virgole che punti
        val qtaInserita = quantitaTesto.replace(",", ".").toDoubleOrNull() ?: 0.0

        return if (tipoTransazione == "SELL") {
            (quantitaPossedutaAsset - qtaInserita) >= 0 && qtaInserita > 0.0
        } else {
            qtaInserita > 0.0
        }
    }


    fun CalcolaTotale(quantitaTesto: String, prezzoGiorno: Double?): Double {
        val qta = quantitaTesto.replace(",", ".").toDoubleOrNull() ?: 0.0
        val prezzo = prezzoGiorno ?: 0.0
        return qta * prezzo
    }
}