package com.example.cryptoinsighta.model

import androidx.room.Embedded

data class TransactionWithTicker(
    @Embedded val transazione: Transaction,
    val tickerAsset: String
)
