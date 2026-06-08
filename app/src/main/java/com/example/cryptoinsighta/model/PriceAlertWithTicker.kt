package com.example.cryptoinsighta.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class PriceAlertWithTicker(
    @Embedded val priceAlert: PriceAlert,
    @ColumnInfo(name = "ASSE_ticker") //questo forza Room a mappare questa colonna perchè non ho capito perchè non andava
    val tickerAsset: String
)