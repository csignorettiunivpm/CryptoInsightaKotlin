package com.example.cryptoinsighta

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class CryptoCalcTest {

    @Test
    fun testIsBottoneAbilitato_Acquisto() {
        // In un acquisto (BUY) basta che la quantità sia maggiore di zero
        assertTrue(CryptoCalc.IsBottoneAbilitato("0.5", "BUY", 0.0))
        assertTrue(CryptoCalc.IsBottoneAbilitato("1,5", "BUY", 0.0)) // Test conversione virgola
        assertFalse(CryptoCalc.IsBottoneAbilitato("0.0", "BUY", 0.0))
        assertFalse(CryptoCalc.IsBottoneAbilitato("-2", "BUY", 0.0))  // Numero negativo
        assertFalse(CryptoCalc.IsBottoneAbilitato("testo_errato", "BUY", 0.0))
    }

    @Test
    fun testIsBottoneAbilitato_Vendita() {
        // In una vendita (SELL) dobbiamo avere abbastanza fondi nel portafoi
        assertTrue(CryptoCalc.IsBottoneAbilitato("0.5", "SELL", 1.0)) // ho 1.0, vendo 0.5 -> OK
        assertTrue(CryptoCalc.IsBottoneAbilitato("1.0", "SELL", 1.0)) // vendo tutto -> OK
        assertFalse(CryptoCalc.IsBottoneAbilitato("1.5", "SELL", 1.0)) // ho 1.0, vendo 1.5 -> BLOCCO (Saldo insufficiente)
        assertFalse(CryptoCalc.IsBottoneAbilitato("0.0", "SELL", 5.0)) // Quantità zero -> BLOCCO
    }

    @Test
    fun testCalcolaTotale_CalcoloEFormati() {
        // Verifica il calcolo della moltiplicazione e la tolleranza del parsing del testo
        assertEquals(30000.0, CryptoCalc.CalcolaTotale("0.5", 60000.0), 0.001)
        assertEquals(90000.0, CryptoCalc.CalcolaTotale("1,5", 60000.0), 0.001) // Con virgola
        assertEquals(0.0, CryptoCalc.CalcolaTotale("0.0", 60000.0), 0.0)
        assertEquals(0.0, CryptoCalc.CalcolaTotale("0.5", null), 0.0) // Gestione del prezzo nullo/API offline
    }
}